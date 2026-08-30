package com.booking.dto;

import com.booking.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private Long resourceId;
    private String resourceName;
    private String username;
    private BigDecimal price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;

    public ReservationResponse(
            Long id,
            Long resourceId,
            String resourceName,
            String username,
            BigDecimal price,
            LocalDateTime startTime,
            LocalDateTime endTime,
            ReservationStatus status) {

        this.id = id;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.username = username;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
