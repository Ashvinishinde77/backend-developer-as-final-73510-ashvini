package com.booking.service;

import com.booking.dto.ResourceRequest;
import com.booking.dto.ResourceResponse;
import com.booking.entity.Resource;
import com.booking.exception.ResourceNotFoundException;
import com.booking.repository.ResourceRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(
            ResourceRepository resourceRepository) {

        this.resourceRepository =
                resourceRepository;
    }

    public ResourceResponse create(
            ResourceRequest request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setDescription(
                request.getDescription()
        );
        resource.setAvailable(
                request.isAvailable()
        );

        return mapToResponse(
                resourceRepository.save(resource)
        );
    }

    public List<ResourceResponse> getAllResources() {

        return resourceRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ResourceResponse getResource(Long id) {

        Resource resource =
                resourceRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found"
                                )
                        );

        return mapToResponse(resource);
    }

    public ResourceResponse update(
            Long id,
            ResourceRequest request) {

        Resource resource =
                resourceRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found"
                                )
                        );

        resource.setName(request.getName());
        resource.setDescription(
                request.getDescription()
        );
        resource.setAvailable(
                request.isAvailable()
        );

        return mapToResponse(
                resourceRepository.save(resource)
        );
    }

    public void delete(Long id) {

        Resource resource =
                resourceRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found"
                                )
                        );

        resourceRepository.delete(resource);
    }

    private ResourceResponse mapToResponse(
            Resource resource) {

        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                resource.isAvailable()
        );
    }
}
