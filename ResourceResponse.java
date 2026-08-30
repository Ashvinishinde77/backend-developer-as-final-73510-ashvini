package com.booking.dto;

public class ResourceResponse {

    private Long id;
    private String name;
    private String description;
    private boolean available;

    public ResourceResponse(
            Long id,
            String name,
            String description,
            boolean available) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }
}
