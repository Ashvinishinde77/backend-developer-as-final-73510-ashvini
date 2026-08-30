public class ReservationRequest {

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private ReservationStatus status;

    // getters and setters
    if (!request.getEndTime().isAfter(request.getStartTime())) {
    throw new IllegalArgumentException(
        "End time must be after start time"
    );

}
