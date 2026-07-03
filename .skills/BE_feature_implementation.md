# Skill: Spring Boot Feature Implementation (IQFarm)

> **Read this before adding any new backend feature.**
> This skill covers the exact sequence, conventions, and edge cases for
> implementing a full CRUD feature end-to-end in the IQFarm Spring Boot server.

---

## Build Order (strict — never skip a step)

```
Entity → Repository → Service Interface → ServiceImpl → Mapper → DTO (request + response) → Controller
```

If you reverse or interleave these steps you will hit circular-import and
missing-bean errors that are hard to diagnose.

---

## 1. Entity

**Location:** `Server/src/main/java/com/example/server/entity/`

### Rules
- Use **Lombok** `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`.
- Primary key column name must match the pattern `<entity>_id` (e.g. `farm_id`, `crop_id`).
- Audit timestamps (`createdAt`, `updatedAt`) must be managed with `@PrePersist` / `@PreUpdate`
  — **not** `@CreatedDate` / `@LastModifiedDate` (Spring Data Auditing is not enabled).
- Table names are **snake_case plural** (`farms`, `crop_trackers`).
- Column names are **snake_case** (`farm_name`, `region_name`).
- All `@OneToMany` / `@ManyToOne` associations use `fetch = FetchType.LAZY` by default.
- Bidirectional relationships: owning side carries `@JoinColumn`; inverse side carries `mappedBy`.

### Template

```java
@Entity
@Table(name = "farms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farm_id")
    private Long farmId;

    @Column(name = "farm_name", nullable = false)
    private String farmName;

    @Column(nullable = false)
    private String location;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### Common mistakes
- ❌ Forgetting `@Column(name = "farm_id")` — Hibernate defaults to `farm_id` anyway but
  being explicit prevents surprise renames.
- ❌ Using `FetchType.EAGER` on collections — causes N+1 and huge payloads on mobile.
- ❌ Missing `@Builder` — breaks the Mapper pattern (see §4).
- ❌ Returning the entity from a Controller — always map to a DTO.

---

## 2. Repository

**Location:** `Server/src/main/java/com/example/server/repository/`

### Rules
- Extend `JpaRepository<Entity, Long>`.
- Add custom finders using Spring Data method-name conventions first.
- Use `@Query` (JPQL) only when the method name becomes unreadable or requires a JOIN.
- **Never** use native SQL unless JPQL cannot express the query.
- Always return `Optional<T>` from single-item finders.

### Template

```java
@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {

    Optional<Farm> findByFarmOwner_UserId(Long userId);

    boolean existsByFarmOwnerAndFarmName(User owner, String farmName);

    @Query("SELECT f FROM Farm f WHERE f.location = :location")
    List<Farm> findByLocation(@Param("location") String location);
}
```

### Common mistakes
- ❌ `findById` without handling the empty `Optional` — always use `.orElseThrow(...)`.
- ❌ `existsByEmail` vs `findByEmail` — use `existsBy*` when you only need a boolean check
  (avoids fetching the full entity).
- ❌ Joining across lazy associations in a query without a JOIN FETCH — causes
  `LazyInitializationException` outside a transaction.

---

## 3. Service Interface

**Location:** `Server/src/main/java/com/example/server/service/`

### Rules
- One interface per domain entity (e.g. `FarmService`).
- Method signatures use **DTOs**, never entities.
- Naming: input = `XxxRequestDTO`, output = `XxxResponseDTO`.

### Template

```java
public interface FarmService {
    FarmResponseDTO createFarm(CreateFarmRequestDTO request);
    FarmResponseDTO getFarmById(Long id);
    List<FarmResponseDTO> getAllFarms();
    FarmResponseDTO updateFarm(Long id, UpdateFarmRequestDTO request);
    void deleteFarm(Long id);
}
```

---

## 4. ServiceImpl

**Location:** `Server/src/main/java/com/example/server/service/impl/`

### Rules
- Annotate with `@Service`.
- Inject dependencies via constructor (not field injection).
- All business logic lives here — never in controllers or repositories.
- Throw `RuntimeException` with a descriptive message for now (a global exception
  handler will be wired later; for custom exceptions see §7).
- Password hashing: use `PasswordEncryption.encrypt()` / `PasswordEncryption.checkPassword()`
  from `service/impl/PasswordEncryption.java` — do **not** roll your own.
- Re-encrypt passwords on update: call `PasswordEncryption.encrypt()` before `save()`.

### Template

```java
@Service
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final FarmMapper farmMapper;

    public FarmServiceImpl(FarmRepository farmRepository, FarmMapper farmMapper) {
        this.farmRepository = farmRepository;
        this.farmMapper = farmMapper;
    }

    @Override
    public FarmResponseDTO createFarm(CreateFarmRequestDTO request) {
        Farm farm = farmMapper.toEntity(request);
        Farm saved = farmRepository.save(farm);
        return farmMapper.toResponseDTO(saved);
    }

    @Override
    public FarmResponseDTO getFarmById(Long id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farm not found with id: " + id));
        return farmMapper.toResponseDTO(farm);
    }

    // ... getAllFarms, updateFarm, deleteFarm
}
```

### Common mistakes
- ❌ Calling `farmRepository.save()` without returning the saved entity — Hibernate
  may assign the ID only after `save()`, so always use `Farm saved = farmRepository.save(farm)`.
- ❌ Mutating the entity returned by `findById` then forgetting to call `save()`.
- ❌ Updating password on `updateUser` without re-hashing it (see `UserServiceImpl.updateUser`
  — this bug already exists and must be fixed for every entity with a credential field).

---

## 5. Mapper

**Location:** `Server/src/main/java/com/example/server/mapper/`

### Rules
- Annotate with `@Component` (Spring-managed, injected into ServiceImpl).
- Two methods: `toEntity(RequestDTO)` and `toResponseDTO(Entity)`.
- Always null-guard: `if (request == null) return null;`.
- Use the entity's Lombok `@Builder` — never use setters in the mapper.
- Never expose the raw password in `toResponseDTO`.

### Template

```java
@Component
public class FarmMapper {

    public Farm toEntity(CreateFarmRequestDTO request) {
        if (request == null) return null;
        return Farm.builder()
                .farmName(request.getFarmName())
                .location(request.getLocation())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .build();
    }

    public FarmResponseDTO toResponseDTO(Farm farm) {
        if (farm == null) return null;
        return FarmResponseDTO.builder()
                .farmId(farm.getFarmId())
                .farmName(farm.getFarmName())
                .location(farm.getLocation())
                .longitude(farm.getLongitude())
                .latitude(farm.getLatitude())
                .createdAt(farm.getCreatedAt())
                .updatedAt(farm.getUpdatedAt())
                .build();
    }
}
```

### Common mistakes
- ❌ Using `new Farm()` + setters — breaks if Lombok `@Builder` is the only way to
  set all fields in a consistent order.
- ❌ Including the entity's `@OneToMany` collection in `toResponseDTO` without
  limiting depth — causes infinite recursion or enormous JSON payloads.
- ❌ Forgetting `@Component` — Spring won't find it and ServiceImpl will fail to start.

---

## 6. DTOs

**Location:**
- Requests: `Server/src/main/java/com/example/server/dto/request/`
- Responses: `Server/src/main/java/com/example/server/dto/response/`

### Request DTO rules
- Annotate with Lombok `@Data` (generates getters, setters, equals, hashCode).
- Add Bean Validation annotations (`@NotBlank`, `@NotNull`, `@Email`, `@Min`, etc.).
- Name: `<Action><Entity>RequestDTO` — e.g. `CreateFarmRequestDTO`, `UpdateFarmRequestDTO`.

```java
@Data
public class CreateFarmRequestDTO {

    @NotBlank(message = "Farm name is required")
    private String farmName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    private Double latitude;
}
```

### Response DTO rules
- Annotate with Lombok `@Data @Builder @NoArgsConstructor @AllArgsConstructor`.
- Include `createdAt` and `updatedAt` as `LocalDateTime`.
- Never include password or internal IDs that the client doesn't need.
- Name: `<Entity>ResponseDTO` — e.g. `FarmResponseDTO`.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmResponseDTO {
    private Long farmId;
    private String farmName;
    private String location;
    private Double longitude;
    private Double latitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Common mistakes
- ❌ Using `@Data` on a Response DTO without `@Builder` — the Mapper can't use the
  builder pattern.
- ❌ Missing `@NoArgsConstructor` / `@AllArgsConstructor` on Response DTOs — Jackson
  can't deserialize/serialize.
- ❌ Putting validation annotations on Response DTOs — they belong only on Request DTOs.

---

## 7. Controller

**Location:** `Server/src/main/java/com/example/server/controller/`

### Rules
- Annotate with `@RestController` and `@RequestMapping("/api/<resource>")`.
- Base path must start with `/api/` (not `/api/v1/` — the current codebase uses `/api/`).
- Inject the **Service interface**, never the implementation.
- Always wrap responses in `ApiResponse<T>` (see §8).
- Annotate request body parameters with `@Valid` to trigger Bean Validation.
- Use `@CrossOrigin` at controller level to allow `http://localhost:4200`.

### CRUD endpoint pattern

```java
@RestController
@RequestMapping("/api/farms")
@CrossOrigin(origins = "http://localhost:4200")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FarmResponseDTO>> createFarm(
            @Valid @RequestBody CreateFarmRequestDTO request) {
        FarmResponseDTO farm = farmService.createFarm(request);
        return new ResponseEntity<>(
                ApiResponse.success("Farm created successfully", farm),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FarmResponseDTO>> getFarmById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(farmService.getFarmById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FarmResponseDTO>>> getAllFarms() {
        return ResponseEntity.ok(ApiResponse.success(farmService.getAllFarms()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FarmResponseDTO>> updateFarm(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFarmRequestDTO request) {
        return ResponseEntity.ok(
                ApiResponse.success("Farm updated successfully", farmService.updateFarm(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFarm(@PathVariable Long id) {
        farmService.deleteFarm(id);
        return ResponseEntity.ok(ApiResponse.success("Farm deleted successfully", null));
    }
}
```

### Common mistakes
- ❌ Injecting `FarmServiceImpl` directly — inject the `FarmService` interface.
- ❌ Returning `201 Created` for GET or PUT — only POST on resource creation returns 201.
- ❌ Omitting `@Valid` on `@RequestBody` — validation annotations are silently ignored.
- ❌ Forgetting `@CrossOrigin` — the Angular client on port 4200 will get CORS errors.

---

## 8. ApiResponse Shape

All controllers return `ResponseEntity<ApiResponse<T>>`.

**`ApiResponse.java` factory methods (already implemented):**

```java
// Success with data only
ApiResponse.success(data)                    // message = "Success"

// Success with custom message + data
ApiResponse.success("Farm created", farm)

// Error
ApiResponse.error("Farm not found")          // success = false, data = null
```

**Wire format:**
```json
// Success
{ "success": true,  "message": "Farm created successfully", "data": { ... } }

// Error
{ "success": false, "message": "Farm not found with id: 42", "data": null }
```

---

## 9. Enums

**Location:** `Server/src/main/java/com/example/server/enums/`

- Use Java enums for bounded domain values (e.g. `CropStatus`, `UserRole`).
- Store as `@Enumerated(EnumType.STRING)` — **never** `EnumType.ORDINAL` (ordinal breaks
  on reorder).

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private CropStatus status;
```

---

## 10. Verification Checklist

Before finishing a BE feature, confirm each step:

- [ ] Entity compiles with `./mvnw compile`
- [ ] Repository finders return `Optional<T>` for single items
- [ ] ServiceImpl handles `Optional.empty()` with a descriptive `RuntimeException`
- [ ] Mapper has null guards on both methods
- [ ] Request DTO has `@NotBlank` / `@NotNull` on all required fields
- [ ] Controller uses `@Valid` on every `@RequestBody`
- [ ] Controller returns `ApiResponse<T>` on every endpoint
- [ ] `@CrossOrigin(origins = "http://localhost:4200")` is present
- [ ] `./mvnw verify` passes with zero errors
- [ ] Manually tested with curl or Postman (happy path + missing-field error path)

---

## Quick curl Test Commands

```bash
# Register
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Lebo","surname":"Mokoena","email":"lebo@farm.co.za","password":"secret"}' | jq

# Login
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"lebo@farm.co.za","password":"secret"}' | jq

# Get user by ID
curl -s http://localhost:8080/api/users/1 | jq

# Missing field (should return 400)
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"","email":"bad"}' | jq
```
