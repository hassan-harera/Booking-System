package com.englizya.trip.caching

import com.englizya.main.model.TripEntity
import com.englizya.trip.repository.TripRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

interface TripCaching {
    fun getAllTrips(): List<TripEntity>
}

@Service
open class TripCachingImpl(
    private val tripRepository: TripRepository
) : TripCaching {

    override fun getAllTrips(): List<TripEntity> {
        return tripRepository.findAll()
    }
}