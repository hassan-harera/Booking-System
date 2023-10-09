package com.harera.bookingsystem.repository;

import com.englizya.main.model.TripEntity;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface
TripRepository extends JpaRepository<TripEntity, Integer> {

    List<TripEntity> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndTripStatusId(Date date1, Date date2, Integer tripStatusId);

    @Query("select r.lineId from TripEntity r where r.tripId = ?1")
    int getLineId(Integer tripId);

    @Query("select r.pathType from TripEntity r where r.tripId = ?1")
    int getPathType(Integer tripId);

    @Query("select r.lineId from TripEntity r where r.startDate >= ?1 and r.endDate <= ?1")
    List<Integer> recommendLines(Date date);

    @Query("select r.serviceDegree from TripEntity r where r.tripId = ?1")
    Integer findTripServiceDegreeId(@Nullable Integer tripId);

    @Query("select t.tripName from TripEntity t where t.tripId = ?1")
    String getTripName(int tripId);

    @Query("select t.setNumber from TripEntity t where t.tripId = ?1")
    Integer getBusSeatsCount(int tripId);

    @Query("select t.serviceDegree from TripEntity t where t.tripId = ?1")
    int getServiceDegree(Integer tripId);

    @Query("select t.tripId from TripEntity t")
    List<Integer> getTripIdList(Pageable pageable);
}
