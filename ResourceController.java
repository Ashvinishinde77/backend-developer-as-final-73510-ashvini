@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<ResourceResponse> getAll() {
        return resourceService.getAllResources();
    }

    @GetMapping("/{id}")
    public ResourceResponse getById(
            @PathVariable Long id) {

        return resourceService.getResource(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponse> create(
            @Valid @RequestBody ResourceRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resourceService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {

        return resourceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        resourceService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
