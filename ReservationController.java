package com.booking.controller;

import com.booking.dto.ReservationRequest;
import com.booking.dto.ReservationResponse;
import com.booking.entity.ReservationStatus;
import com.booking.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService =
                reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse>
    createReservation(
            @AuthenticationPrincipal
            UserDetails userDetails,

            @Valid
            @RequestBody
            ReservationRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        reservationService
                                .createReservation(
                                        userDetails.getUsername(),
                                        request
                                )
                );
    }

    @GetMapping
    public Page<ReservationResponse>
    getReservations(

            @RequestParam(required = false)
            ReservationStatus status,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction,

            @AuthenticationPrincipal
            UserDetails userDetails) {

        return reservationService
                .getReservations(
                        status,
                        minPrice,
                        maxPrice,
                        page,
                        size,
                        sortBy,
                        direction,
                        userDetails.getUsername()
                );
    }

    @GetMapping("/{id}")
    public ReservationResponse
    getReservationById(
            @PathVariable Long id,

            @AuthenticationPrincipal
            UserDetails userDetails) {

        return reservationService
                .getReservationById(
                        id,
                        userDetails.getUsername()
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationResponse
    updateReservation(
            @PathVariable Long id,

            @Valid
            @RequestBody
            ReservationRequest request) {

        return reservationService
                .updateReservation(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void>
    deleteReservation(
            @PathVariable Long id) {

        reservationService
                .deleteReservation(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
