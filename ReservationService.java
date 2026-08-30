package com.booking.service;

import com.booking.dto.ReservationRequest;
import com.booking.dto.ReservationResponse;

import com.booking.entity.*;

import com.booking.exception.ResourceNotFoundException;

import com.booking.repository.ReservationRepository;
import com.booking.repository.ResourceRepository;
import com.booking.repository.UserRepository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository) {

        this.reservationRepository =
                reservationRepository;

        this.resourceRepository =
                resourceRepository;

        this.userRepository =
                userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(
            String username,
            ReservationRequest request) {

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Resource resource =
                resourceRepository
                        .findById(request.getResourceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found"
                                )
                        );

        if (!resource.isAvailable()) {
            throw new IllegalArgumentException(
                    "Resource is not available"
            );
        }

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        Reservation reservation =
                new Reservation();

        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setPrice(request.getPrice());
        reservation.setStartTime(
                request.getStartTime()
        );
        reservation.setEndTime(
                request.getEndTime()
        );

        reservation.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : ReservationStatus.PENDING
        );

        Reservation saved =
                reservationRepository
                        .save(reservation);

        return mapToResponse(saved);
    }

    public Page<ReservationResponse> getReservations(
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String direction,
            String username) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        boolean isAdmin =
                user.getRole() == Role.ADMIN;

        Pageable pageable = createPageable(
                page,
                size,
                sortBy,
                direction
        );

        Specification<Reservation> specification =
                (root, query, cb) -> {

                    List<Predicate> predicates =
                            new ArrayList<>();

                    if (!isAdmin) {

                        predicates.add(
                                cb.equal(
                                        root.get("user"),
                                        user
                                )
                        );
                    }

                    if (status != null) {

                        predicates.add(
                                cb.equal(
                                        root.get("status"),
                                        status
                                )
                        );
                    }

                    if (minPrice != null) {

                        predicates.add(
                                cb.greaterThanOrEqualTo(
                                        root.get("price"),
                                        minPrice
                                )
                        );
                    }

                    if (maxPrice != null) {

                        predicates.add(
                                cb.lessThanOrEqualTo(
                                        root.get("price"),
                                        maxPrice
                                )
                        );
                    }

                    return cb.and(
                            predicates.toArray(
                                    new Predicate[0]
                            )
                    );
                };

        return reservationRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }

    public ReservationResponse getReservationById(
            Long id,
            String username) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reservation not found"
                                )
                        );

        User currentUser =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isOwner =
                reservation.getUser()
                        .getUsername()
                        .equals(username);

        if (!isAdmin && !isOwner) {

            throw new AccessDeniedException(
                    "You cannot access this reservation"
            );
        }

        return mapToResponse(reservation);
    }

    public ReservationResponse updateReservation(
            Long id,
            ReservationRequest request) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reservation not found"
                                )
                        );

        Resource resource =
                resourceRepository
                        .findById(request.getResourceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found"
                                )
                        );

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        reservation.setResource(resource);
        reservation.setPrice(request.getPrice());
        reservation.setStartTime(
                request.getStartTime()
        );
        reservation.setEndTime(
                request.getEndTime()
        );

        if (request.getStatus() != null) {
            reservation.setStatus(
                    request.getStatus()
            );
        }

        return mapToResponse(
                reservationRepository.save(reservation)
        );
    }

    public void deleteReservation(Long id) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reservation not found"
                                )
                        );

        reservationRepository.delete(reservation);
    }

    private Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (sortBy == null || sortBy.isBlank()) {

            return PageRequest.of(page, size);
        }

        Set<String> allowedFields =
                Set.of(
                        "price",
                        "startTime",
                        "endTime",
                        "status"
                );

        if (!allowedFields.contains(sortBy)) {

            throw new IllegalArgumentException(
                    "Invalid sort field"
            );
        }

        Sort sort =
                "desc".equalsIgnoreCase(direction)
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();

        return PageRequest.of(
                page,
                size,
                sort
        );
    }

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getResource().getId(),
                reservation.getResource().getName(),
                reservation.getUser().getUsername(),
                reservation.getPrice(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }
}
