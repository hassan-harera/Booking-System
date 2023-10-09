package com.harera.bookingsystem.controller;


import com.englizya.main.model.TripEntity;
import com.englizya.main.request.TripSearchRequest;
import com.englizya.trip.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TripEntity>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getAllTrips(
            @PathVariable("tripId") @NotNull Integer tripId
    ) {
        return ResponseEntity.ok(tripService.getTrip(tripId));
    }

    @PostMapping("/search")
    public ResponseEntity<?> getTrips(
            @RequestBody @NotNull TripSearchRequest request
    ) {
        return ResponseEntity.ok(tripService.searchTrips(request));
    }
}
