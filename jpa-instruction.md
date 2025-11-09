### Basic Service
```java
@Service
@Transactional(readOnly = true)
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    // Option 1: MapStruct mapping (entity to DTO)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(user# Spring Boot Pageable, JPA & Hibernate Best Practices

## 1. Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

## 2. Application Properties
```properties
# SQL Logging (Dev only)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Performance
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

## 3. Base Entity (Soft Delete)
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean deleted = false;
    
    private LocalDateTime deletedAt;
}

@Configuration
@EnableJpaAuditing
public class JpaConfig {}
```

## 4. Entity with Indexes
```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status_deleted", columnList = "status, deleted")
})
@SQLDelete(sql = "UPDATE users SET deleted = true, deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted = false")
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
```

## 5. DTOs and MapStruct Mappers

### DTOs
```java
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
    private String departmentName;
    // No orders list - avoid lazy loading
}

@Data
public class OrderDTO {
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Long userId;
    private String userName;
    // No items list - avoid lazy loading
}

@Data
public class OrderDetailDTO {
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private UserDTO user;
    private List<OrderItemDTO> items; // Only when explicitly loaded
}
```

### MapStruct Mappers
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    // Skip uninitialized collections automatically
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "orders", ignore = true) // Skip lazy collection
    UserDTO toDTO(User user);
    
    List<UserDTO> toDTOList(List<User> users);
}

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "items", ignore = true) // Skip lazy collection
    OrderDTO toDTO(Order order);
    
    // Use this only when items are JOIN FETCHed
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    OrderDetailDTO toDetailDTO(Order order);
    
    List<OrderDTO> toDTOList(List<Order> orders);
}
```

### Projection Interfaces (Alternative)
```java
// Use when you want DB-level projection (more efficient)
public interface UserProjection {
    Long getId();
    String getName();
    String getEmail();
    UserStatus getStatus();
}

public interface OrderProjection {
    Long getId();
    BigDecimal getTotalAmount();
    OrderStatus getStatus();
    
    UserProjection getUser();
    
    interface UserProjection {
        Long getId();
        String getName();
    }
}
```

## 6. Repository with Projections

### Basic Repository
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Simple pagination
    Page<User> findAll(Pageable pageable);
    
    // Projection pagination (no N+1)
    @Query("SELECT u.id as id, u.name as name, u.email as email, u.status as status FROM User u")
    Page<UserProjection> findAllProjected(Pageable pageable);
    
    // ID-first pattern for search
    @Query("SELECT u.id FROM User u WHERE u.name LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<Long> searchIds(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(@Param("ids") List<Long> ids);
    
    // Soft delete queries
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdIncludingDeleted(@Param("id") Long id);
}
```

### Advanced Repository with DTO Projection
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Constructor projection (single query, no N+1)
    @Query("SELECT new com.example.dto.OrderDTO(o.id, u.name, o.totalAmount) " +
           "FROM Order o JOIN o.user u WHERE o.status = :status")
    Page<OrderDTO> findByStatusWithUser(@Param("status") OrderStatus status, Pageable pageable);
    
    // ID-first for complex search
    @Query("SELECT o.id FROM Order o JOIN o.user u " +
           "WHERE (u.name LIKE %:keyword% OR o.id = :orderId) AND o.status = :status")
    Page<Long> searchIds(@Param("keyword") String keyword, 
                          @Param("orderId") Long orderId,
                          @Param("status") OrderStatus status, 
                          Pageable pageable);
    
    // Fetch with JOIN FETCH (prevents N+1)
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id IN :ids")
    List<Order> findByIdsWithUser(@Param("ids") List<Long> ids);
    
    // Interface projection
    @Query("SELECT o.id as id, o.totalAmount as totalAmount, " +
           "o.status as status, o.user as user FROM Order o")
    Page<OrderProjection> findAllProjected(Pageable pageable);
}
```

## 7. Service Layer

### Basic Service
```java
@Service
@Transactional(readOnly = true)
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    // Option 1: MapStruct mapping (entity to DTO)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toDTO); // Lazy collections ignored by MapStruct
    }
    
    // Option 2: Projection (DB-level, more efficient)
    public Page<UserProjection> findAllProjected(Pageable pageable) {
        return userRepository.findAllProjected(pageable);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    
    // Write operations
    @Transactional
    public UserDTO create(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setStatus(dto.getStatus());
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }
    
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id); // Soft delete via @SQLDelete
    }
}
```

### ID-First Search Pattern
```java
@Service
@Transactional(readOnly = true)
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * ID-First Pattern with MapStruct:
     * 1. Search IDs (lightweight)
     * 2. Fetch entities with JOIN FETCH
     * 3. Map to DTO (lazy collections ignored)
     */
    public Page<OrderDTO> search(String keyword, OrderStatus status, Pageable pageable) {
        // Step 1: Get IDs
        Page<Long> idPage = orderRepository.searchIds(keyword, null, status, pageable);
        
        if (idPage.isEmpty()) {
            return Page.empty(pageable);
        }
        
        // Step 2: Batch fetch with JOIN FETCH (only user, not items)
        List<Long> ids = idPage.getContent();
        List<Order> orders = orderRepository.findByIdsWithUser(ids);
        
        // Step 3: Preserve order and map to DTO
        Map<Long, Order> orderMap = orders.stream()
            .collect(Collectors.toMap(Order::getId, Function.identity()));
        
        List<OrderDTO> dtos = ids.stream()
            .map(orderMap::get)
            .filter(Objects::nonNull)
            .map(orderMapper::toDTO) // MapStruct ignores uninitialized items
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, idPage.getTotalElements());
    }
    
    /**
     * Get detail with items (explicit JOIN FETCH)
     */
    public OrderDetailDTO findDetailById(Long id) {
        Order order = orderRepository.findByIdWithUserAndItems(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return orderMapper.toDetailDTO(order); // Items are initialized
    }
    
    /**
     * Projection alternative (DB-level, no lazy loading issues)
     */
    public Page<OrderProjection> searchProjected(OrderStatus status, Pageable pageable) {
        return orderRepository.findAllProjected(status, pageable);
    }
}
```

## 8. REST Controller
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Return Page<DTO> (MapStruct handles mapping)
    @GetMapping
    public Page<UserDTO> getUsers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return userService.findAll(pageable);
    }
    
    // Or return Page<Projection> (DB-level projection)
    @GetMapping("/projected")
    public Page<UserProjection> getUsersProjected(Pageable pageable) {
        return userService.findAllProjected(pageable);
    }
    
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return userMapper.toDTO(user);
    }
    
    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO dto) {
        return userService.create(dto);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/search")
    public Page<OrderDTO> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        return orderService.search(keyword, status, pageable);
    }
    
    @GetMapping("/{id}/detail")
    public OrderDetailDTO getDetail(@PathVariable Long id) {
        return orderService.findDetailById(id); // With items
    }
}
```

## 9. Specifications (Dynamic Filtering)
```java
public class UserSpecifications {
    
    public static Specification<User> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null) return null;
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }
    
    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }
}

// In Repository
public interface UserRepository extends JpaRepository<User, Long>, 
                                        JpaSpecificationExecutor<User> {
    // Now you can use:
    // Page<User> findAll(Specification<User> spec, Pageable pageable);
}

// In Service
public Page<User> search(String keyword, UserStatus status, Pageable pageable) {
    Specification<User> spec = Specification
        .where(UserSpecifications.hasKeyword(keyword))
        .and(UserSpecifications.hasStatus(status));
    return userRepository.findAll(spec, pageable);
}
```

## 10. Transaction Best Practices

### Service-Level Transactions
```java
@Service
@Transactional(readOnly = true)  // Default for all methods
public class OrderService {
    
    // Read operations use read-only
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }
    
    // Override for writes
    @Transactional
    public Order create(OrderDTO dto) {
        Order order = new Order();
        // ... set properties
        return orderRepository.save(order);
    }
    
    // Batch operations
    @Transactional
    public void createBatch(List<OrderDTO> dtos) {
        List<Order> orders = dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
        orderRepository.saveAll(orders);
        orderRepository.flush(); // Force batch execution
    }
}
```

### Transaction Propagation
```java
@Service
public class OrderService {
    
    @Autowired
    private NotificationService notificationService;
    
    @Transactional
    public void createOrder(OrderDTO dto) {
        Order order = saveOrder(dto);
        
        // Runs in separate transaction (won't rollback if parent fails)
        notificationService.sendEmail(order.getId());
    }
}

@Service
public class NotificationService {
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendEmail(Long orderId) {
        // Always commits independently
    }
}
```

## 11. Soft Delete Operations

### Repository
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Auto excludes deleted (via @Where clause)
    Page<Product> findAll(Pageable pageable);
    
    // Include deleted
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdIncludingDeleted(@Param("id") Long id);
    
    // Restore
    @Modifying
    @Query("UPDATE Product p SET p.deleted = false, p.deletedAt = null WHERE p.id = :id")
    void restore(@Param("id") Long id);
    
    // Hard delete
    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :id")
    void hardDelete(@Param("id") Long id);
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public class ProductService {
    
    @Transactional
    public void softDelete(Long id) {
        productRepository.deleteById(id); // Uses @SQLDelete
    }
    
    @Transactional
    public void restore(Long id) {
        productRepository.restore(id);
    }
    
    @Transactional
    public void hardDelete(Long id) {
        productRepository.hardDelete(id);
    }
}
```

## 12. N+1 Query Prevention

### Problem
```java
// BAD: N+1 queries
List<Order> orders = orderRepository.findAll();
orders.forEach(o -> System.out.println(o.getUser().getName())); // +N queries
```

### Solutions

**1. Projections (Recommended)**
```java
@Query("SELECT o.id as id, u.name as userName, o.totalAmount as totalAmount " +
       "FROM Order o JOIN o.user u")
Page<OrderProjection> findAllWithUser(Pageable pageable);
```

**2. JOIN FETCH**
```java
@Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id IN :ids")
List<Order> findByIdsWithUser(@Param("ids") List<Long> ids);
```

**3. Batch Fetching**
```java
@Entity
public class Order {
    @ManyToOne(fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private User user;
}
```

## 13. Performance Tips

### Indexing
```sql
-- Composite indexes
CREATE INDEX idx_users_status_created ON users(status, created_at);
CREATE INDEX idx_orders_user_status ON orders(user_id, status);

-- Covering index for searches
CREATE INDEX idx_users_search ON users(name, email) INCLUDE (id);
```

### Query Optimization
```java
// Use projections for list views
@Query("SELECT u.id as id, u.name as name FROM User u")
Page<UserProjection> findAllLight(Pageable pageable);

// Use full entity only when needed
@Query("SELECT u FROM User u WHERE u.id = :id")
Optional<User> findByIdFull(@Param("id") Long id);

// Native query for complex aggregations
@Query(value = "SELECT user_id, COUNT(*) as count FROM orders GROUP BY user_id", 
       nativeQuery = true)
List<Object[]> getOrderCounts();
```

### Caching
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users");
    }
}

@Service
@CacheConfig(cacheNames = "users")
public class UserService {
    
    @Cacheable(key = "#id")
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
    
    @CacheEvict(key = "#id")
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
```

## 14. Request Examples

```bash
# Basic pagination
GET /api/users?page=0&size=20&sort=name,asc

# Multiple sort
GET /api/users?page=0&size=20&sort=status,asc&sort=createdAt,desc

# Search with filters
GET /api/orders/search?keyword=john&status=COMPLETED&page=0&size=15
```

## 15. Key Rules

✅ **DO**
- **MapStruct:** Use `@Mapping(target = "lazyCollection", ignore = true)` to skip uninitialized collections
- **MapStruct:** Set `unmappedTargetPolicy = ReportingPolicy.IGNORE` for safety
- Use projections for list views (more efficient than entity + MapStruct)
- Use MapStruct when you need entity manipulation or complex mapping logic
- Use ID-first pattern for complex searches
- Default to `@Transactional(readOnly = true)`
- Use JOIN FETCH only for fields you'll actually use
- Add indexes on filtered/sorted columns
- Return `Page<DTO>` or `Page<Projection>` from controllers

❌ **DON'T**
- Never access lazy collections without JOIN FETCH
- Never use `FetchType.EAGER`
- Never load all data then paginate in memory
- Never map uninitialized collections (causes LazyInitializationException)
- Never forget `@Transactional` on write operations
- Never skip indexes on foreign keys

### MapStruct vs Projection Decision Tree
```
Need full entity for updates/deletes?
├─ YES → Use Entity + MapStruct (ignore lazy collections)
└─ NO → Need complex mapping logic?
    ├─ YES → Use Entity + MapStruct
    └─ NO → Use Projection (best performance)
```