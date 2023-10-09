package com.englizya.trip.service

import com.englizya.booking.repository.ReservationRepository
import com.englizya.bookingseat.service.ReservationSeatService
import com.englizya.main.model.TripEntity
import com.englizya.main.model.TripTimes
import com.englizya.main.repository.LineStationRepository
import com.englizya.main.request.TripSearchRequest
import com.englizya.trip.caching.TripCaching
import com.englizya.trip.repository.TripRepository
import com.englizya.utils.TimeUtils
import io.ktor.util.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.springframework.stereotype.Service
import java.util.*

interface TripService {

    fun getTrip(tripId: Int): TripEntity
    fun getAllTrips(): List<TripEntity>
    fun searchTrips(request: TripSearchRequest): List<TripEntity>
    fun getRecommendedLines(): List<Int>
    fun checkTripTime(tripTimes: MutableList<TripTimes>): Boolean
}

@InternalAPI
@Service
class TripServiceImpl(
    private val tripRepository: TripRepository,
    private val tripCaching: TripCaching,
    private val seatReservationService: ReservationSeatService,
    private val reservationRepository: ReservationRepository,
    private val lineStationRepository: LineStationRepository,
) : TripService {


    override fun getTrip(tripId: Int): TripEntity {
        return tripRepository.findById(tripId).get()
    }

    override fun getAllTrips(): List<TripEntity> {
        return tripCaching.getAllTrips()
    }

    override fun searchTrips(request: TripSearchRequest): List<TripEntity> {
        var dateTime = kotlin.runCatching { DateTime.parse(request.date) }.getOrNull() ?: return listOf()
        dateTime = DateTime(
            dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth, 0, 0, 0, 0,
            DateTimeZone.forTimeZone(TimeZone.getDefault())
        )
        val date = dateTime.toDate()

        val nowDateTime = DateTime(
            DateTime.now().year,
            DateTime.now().monthOfYear,
            DateTime.now().dayOfMonth,
            0,
            0,
            0,
            0,
            DateTimeZone.forTimeZone(TimeZone.getDefault())
        )
        var isSearchingToday = true
        if (dateTime.millis - nowDateTime.millis >= TimeUtils.MILLIS_IN_DAY) {
            isSearchingToday = false
        } else if (nowDateTime.millis - dateTime.millis >= TimeUtils.MILLIS_IN_DAY) {
            return listOf()
        }


        if (request.sourceStationId == request.destinationStationId)
            return emptyList()

        tripCaching.getAllTrips().filter {
            it.tripStatusId == 1 &&
                    it.startDate!!.time <= date.time &&
                    it.endDate!!.time >= date.time
        }.filter { tripEntity ->
            var stations = lineStationRepository.getLineStations(tripEntity.lineId!!, tripEntity.pathType!!)
                .sortedBy { it.startDate }

            if (isSearchingToday) {
                if (checkTripTime(tripEntity.tripTimes).not()) {
                    return@filter false
                }
            }

            stations = stations.filter {
                (it.branch.branchId == request.sourceStationId) ||
                        (it.branch.branchId == request.destinationStationId)
            }

            tripEntity.stations = arrayListOf()
            tripEntity.stations!!.addAll(stations)

            tripEntity.tripId?.let {
                reservationRepository
                    .getTripReservations(it, date)
                    .get()
                    .also {
                        tripEntity.reservations.addAll(it)
                    }
            }

            if (stations.size < 2)
                return@filter false

            val source = stations.first {
                it.branch.branchId == request.sourceStationId
            }

            val destination = stations.first {
                it.branch.branchId == request.destinationStationId
            }

            if (source.stationOrder > destination.stationOrder)
                return@filter false

            tripEntity.reservations.isNotEmpty()
        }.map { tripEntity ->
            seatReservationService.updateSeatStatus(
                tripEntity,
                request.sourceStationId,
                request.destinationStationId
            )
            tripEntity
        }.let {
            return it
        }
    }

    override fun getRecommendedLines(): List<Int> {
        return tripRepository.recommendLines(Date())
    }

    override fun checkTripTime(tripTimes: MutableList<TripTimes>): Boolean {
        tripTimes.sortedBy { it.startTime }.also {
            if (it.isEmpty())
                return false

            it.first().startTime!!.toLocalDateTime().hour.minus(DateTime.now().hourOfDay).also {
                if (it < 2) {
                    return false
                }
            }
        }


        return true
    }
}