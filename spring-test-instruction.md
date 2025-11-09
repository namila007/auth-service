# Spring Boot Testing Best Practices Guide

## 1. Dependencies

```xml
<!-- Test Dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>

<!-- H2 for in-memory testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## 2. Unit Tests (Service Layer)

**Purpose:** Test business logic in isolation with mocked dependencies

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void createUser_Success() {
        // Given
        UserDTO dto = new UserDTO("test@email.com", "Test User");
        User user = new User();
        user.setId(1L);
        
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(dto);
        
        // When
        UserDTO result = userService.create(dto);
        
        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(user);
    }
    
    @Test
    void findById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> userService.findById(1L));
    }
}
```

**Key Points:**
- Use `@ExtendWith(MockitoExtension.class)` - no Spring context
- Mock all dependencies with `@Mock`
- Use `@InjectMocks` for service under test
- Test only business logic, not database or Spring features
- Fast execution (no context loading)

## 3. Repository Tests with H2

**Purpose:** Test JPA queries with in-memory database

### application-test.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Repository Test
```java
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void findByEmail_ExistingUser_ReturnsUser() {
        // Given
        User user = new User();
        user.setEmail("test@email.com");
        user.setName("Test");
        entityManager.persist(user);
        entityManager.flush();
        
        // When
        Optional<User> found = userRepository.findByEmail("test@email.com");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());
    }
    
    @Test
    void searchIds_WithKeyword_ReturnsPaginatedIds() {
        // Given
        User user1 = new User("john@test.com", "John Doe");
        User user2 = new User("jane@test.com", "Jane Smith");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
        
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Long> ids = userRepository.searchIds("john", pageable);
        
        // Then
        assertEquals(1, ids.getTotalElements());
    }
}
```

**Key Points:**
- Use `@DataJpaTest` - loads only JPA components
- Automatically uses H2 in-memory database
- Auto-rollback after each test
- `TestEntityManager` for test data setup
- Tests custom queries, pagination, projections

## 4. Repository Tests with Testcontainers

**Purpose:** Test with real PostgreSQL database

### Base Test Configuration
```java
@Testcontainers
@ActiveProfiles("testcontainers")
public abstract class AbstractContainerTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

### Repository Test
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryContainerTest extends AbstractContainerTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testPostgresSpecificFunction() {
        // Test PostgreSQL-specific features
        User user = new User("test@email.com", "Test");
        userRepository.save(user);
        
        // Test custom native query with PostgreSQL functions
        List<User> results = userRepository.findWithCustomFunction();
        
        assertFalse(results.isEmpty());
    }
}
```

**Key Points:**
- Extend `AbstractContainerTest` for PostgreSQL tests
- Use `@AutoConfigureTestDatabase(replace = NONE)` to use Testcontainer
- Test PostgreSQL-specific features (JSON, arrays, full-text search)
- Slower than H2 but more realistic
- Container reused across tests (faster)

## 5. Redis Integration Tests

### Redis Container Configuration
```java
@Testcontainers
@ActiveProfiles("redis-test")
public abstract class AbstractRedisTest {
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }
}
```

### Redis Test
```java
@SpringBootTest
class CacheServiceTest extends AbstractRedisTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Test
    void cacheWorksCorrectly() {
        // First call - hits database
        UserDTO user1 = userService.findById(1L);
        
        // Second call - from cache
        UserDTO user2 = userService.findById(1L);
        
        // Verify cache
        Cache cache = cacheManager.getCache("users");
        assertNotNull(cache.get(1L));
    }
    
    @Test
    void cacheEvictionWorks() {
        userService.findById(1L); // Cache it
        userService.delete(1L); // Should evict
        
        Cache cache = cacheManager.getCache("users");
        assertNull(cache.get(1L));
    }
}
```

## 6. Controller Unit Tests

**Purpose:** Test controller logic without starting server

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void getUsers_ReturnsPagedUsers() throws Exception {
        // Given
        UserDTO user = new UserDTO("test@email.com", "Test");
        Page<UserDTO> page = new PageImpl<>(List.of(user));
        
        when(userService.findAll(any(Pageable.class))).thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].email").value("test@email.com"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    void createUser_ValidInput_ReturnsCreated() throws Exception {
        // Given
        UserDTO dto = new UserDTO("test@email.com", "Test");
        when(userService.create(any(UserDTO.class))).thenReturn(dto);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@email.com\",\"name\":\"Test\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("test@email.com"));
    }
    
    @Test
    void createUser_InvalidInput_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalid\"}"))
            .andExpect(status().isBadRequest());
    }
}
```

**Key Points:**
- Use `@WebMvcTest` - loads only web layer
- Mock service layer with `@MockBean`
- Use `MockMvc` for HTTP requests
- Test validation, status codes, JSON responses
- Fast (no full context, no database)

## 7. Integration Tests (Full Application)

**Purpose:** Test complete application with all layers

### Base Integration Test
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration")
class ApplicationIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    void completeUserFlow_CreateReadUpdateDelete() {
        // Create
        UserDTO createDto = new UserDTO("test@email.com", "Test User");
        ResponseEntity<UserDTO> createResponse = restTemplate.postForEntity(
            "/api/users", createDto, UserDTO.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Long userId = createResponse.getBody().getId();
        
        // Read
        ResponseEntity<UserDTO> getResponse = restTemplate.getForEntity(
            "/api/users/" + userId, UserDTO.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Test User", getResponse.getBody().getName());
        
        // Update
        UserDTO updateDto = new UserDTO("test@email.com", "Updated Name");
        restTemplate.put("/api/users/" + userId, updateDto);
        
        UserDTO updated = restTemplate.getForObject("/api/users/" + userId, UserDTO.class);
        assertEquals("Updated Name", updated.getName());
        
        // Delete
        restTemplate.delete("/api/users/" + userId);
        
        ResponseEntity<UserDTO> deletedResponse = restTemplate.getForEntity(
            "/api/users/" + userId, UserDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
    }
    
    @Test
    void searchWithPagination_ReturnsCorrectResults() {
        // Setup test data
        userRepository.saveAll(List.of(
            new User("john@test.com", "John Doe"),
            new User("jane@test.com", "Jane Smith"),
            new User("bob@test.com", "Bob Johnson")
        ));
        
        // Search
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/users/search?keyword=john&page=0&size=10", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("John"));
        assertTrue(response.getBody().contains("Johnson"));
        assertFalse(response.getBody().contains("Jane"));
    }
}
```

**Key Points:**
- Use `@SpringBootTest(webEnvironment = RANDOM_PORT)` - full context
- Use `TestRestTemplate` for HTTP calls
- Test complete user flows (end-to-end)
- Test multiple components working together
- Slowest tests - run fewer of these

## 8. Test Organization

```
src/test/java
├── unit/
│   ├── service/
│   │   ├── UserServiceTest.java
│   │   └── OrderServiceTest.java
│   └── util/
│       └── ValidationUtilTest.java
├── integration/
│   ├── repository/
│   │   ├── h2/
│   │   │   └── UserRepositoryTest.java
│   │   └── testcontainers/
│   │       └── UserRepositoryContainerTest.java
│   ├── controller/
│   │   └── UserControllerTest.java
│   └── application/
│       └── ApplicationIntegrationTest.java
└── config/
    ├── AbstractContainerTest.java
    └── AbstractRedisTest.java
```

## 9. Test Naming Convention

```java
// Pattern: methodName_condition_expectedResult

@Test
void findById_ExistingUser_ReturnsUser() {}

@Test
void findById_NonExistingUser_ThrowsException() {}

@Test
void create_ValidInput_SavesAndReturnsUser() {}

@Test
void create_DuplicateEmail_ThrowsException() {}
```

## 10. Performance Tips

### Speed Optimization
```java
// Use @Transactional on test classes for auto-rollback
@DataJpaTest
@Transactional
class FastRepositoryTest {
    // Tests auto-rollback, faster than @BeforeEach cleanup
}

// Reuse containers across test classes
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReusableContainerTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withReuse(true); // Reuse container
}

// Use @DirtiesContext sparingly (slow)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ExpensiveTest {
    // Only when absolutely necessary
}
```

### Test Data Builders
```java
public class UserTestBuilder {
    private String email = "test@email.com";
    private String name = "Test User";
    
    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public User build() {
        return new User(email, name);
    }
}

// Usage
User user = new UserTestBuilder()
    .withEmail("john@test.com")
    .build();
```

## 11. Test Profiles

### application-test.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: create-drop
```

### application-integration.yml
```yaml
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
logging:
  level:
    org.springframework: INFO
    com.example: DEBUG
```

## 12. Testing Checklist

### Unit Tests (Fast, Many)
- ✅ Service business logic
- ✅ Utility methods
- ✅ Validators
- ✅ Mappers
- ✅ Exception handling

### Repository Tests
- ✅ Custom queries (H2)
- ✅ Pagination
- ✅ Projections
- ✅ PostgreSQL-specific features (Testcontainers)

### Controller Tests
- ✅ Request/response mapping
- ✅ Validation
- ✅ Status codes
- ✅ Error handling

### Integration Tests (Slow, Few)
- ✅ Complete user flows
- ✅ Cross-layer functionality
- ✅ Cache behavior
- ✅ Transaction boundaries

## 13. Best Practices Summary

### DO ✅
- **Test pyramid:** Many unit tests, fewer integration tests
- **H2 for simple queries**, Testcontainers for PostgreSQL-specific features
- **Mock external dependencies** in unit tests
- **Use test profiles** to separate configurations
- **Test data builders** for complex object creation
- **Reuse containers** across tests for speed
- **Clear test names** describing scenario and expectation
- **One assertion concept per test**

### DON'T ❌
- Don't use `@SpringBootTest` for unit tests (too slow)
- Don't test framework code (Spring Data JPA, etc.)
- Don't share state between tests
- Don't use real external services (APIs, databases)
- Don't skip edge cases and error scenarios
- Don't write integration tests for everything
- Don't use random data (tests should be deterministic)

## 14. Quick Reference

| Test Type | Annotation | Context | Speed | Use Case |
|-----------|-----------|---------|-------|----------|
| Service Unit | `@ExtendWith(MockitoExtension.class)` | None | ⚡️⚡️⚡️ | Business logic |
| Repository H2 | `@DataJpaTest` | JPA only | ⚡️⚡️ | Simple queries |
| Repository Postgres | `@DataJpaTest` + Testcontainers | JPA + DB | ⚡️ | DB-specific features |
| Controller | `@WebMvcTest` | Web layer | ⚡️⚡️ | API endpoints |
| Integration | `@SpringBootTest` | Full | 🐌 | End-to-end flows |

## 15. Example Test Execution Order

```bash
# 1. Unit tests (fastest, run always)
mvn test -Dtest="*Test"

# 2. Repository tests with H2 (fast, run often)
mvn test -Dtest="*RepositoryTest"

# 3. Testcontainer tests (medium, run before commit)
mvn test -Dtest="*ContainerTest"

# 4. Integration tests (slowest, run in CI/CD)
mvn test -Dtest="*IntegrationTest"
```