# E-Commerce API (Spring Boot + JPA)

Este proyecto es una API REST para un e-commerce simple.  
Está desarrollado en **Java 21**, **Spring Boot**, **Spring Data JPA**, **Hibernate** y **MySQL**.

---

## Estructura de carpetas

```
src/main/java/com/uade/tpo/ecommerce
 ├── controllers     # Endpoints REST
 ├── entity          # Entidades JPA (tablas)
 │    └── dto        # DTOs para requests/responses
 ├── exception       # Excepciones personalizadas
 ├── repository      # Acceso a datos (JpaRepository)
 └── service         # Lógica de negocio
      └── impl       # Implementaciones de los servicios
```

---

## Explicación de cada paquete

- **controllers/**  
  Contienen las clases REST Controller. Manejan las rutas HTTP (`@GetMapping`, `@PostMapping`, etc.), reciben DTOs y devuelven JSON.  
  Ejemplo: `UsersController`, `ProductsController`, `OrdersController`.

- **entity/**  
  Contiene las clases JPA que representan las tablas de la base de datos.  
  Ejemplo: `User`, `Product`, `Order`, `Payment`, `Cart`, `OrderItem`.  

  - **entity/dto/**: DTOs usados para entrada/salida de datos.  
    Ejemplo: `UserRequest`, `ProductRequest`, `OrderRequest`.

- **exception/**  
  Excepciones personalizadas para manejar errores de dominio (`UserNotFoundException`, `ProductDuplicateException`, `InsufficientStockException`, etc.).

- **repository/**  
  Interfaces que extienden `JpaRepository`. Proveen métodos CRUD por defecto (`save`, `findById`, `findAll`, `delete`).  
  - Cuando necesitás queries específicas, podés:
    - Usar **métodos derivados**: `findByEmail`, `existsByNameIgnoreCase`.
    - O usar `@Query`: solo se ve en `CategoryRepository` porque buscás por descripción.

- **service/**  
  Lógica de negocio. Define la interfaz (ej. `UserService`) y la implementación (`UserServiceImpl`).  
  Se encargan de validar reglas, armar entidades con `.builder()`, etc.

---

## Anotaciones usadas

### Lombok
- `@Data`: Genera getters, setters, `toString`, `equals`, `hashCode`.
- `@Builder`: Permite instanciar objetos con `.builder()`.
- `@NoArgsConstructor` y `@AllArgsConstructor`: Constructores vacío y con todos los campos.

### JPA/Hibernate
- `@Entity`: Marca una clase como tabla.
- `@Table(name="...")`: Define nombre de tabla (ej. `orders`, `users`, para evitar palabras reservadas).
- `@Id` + `@GeneratedValue`: Clave primaria autoincremental.
- `@Column`: Configuración de columna (nullable, length, unique).
- Relaciones:
  - `@ManyToOne`, `@OneToMany`, `@OneToOne`.
  - `@JoinColumn`: Define la FK.
  - `@Enumerated(EnumType.STRING)`: Guarda enums como texto.

### Spring
- `@RestController`: Expone clase como endpoint REST.
- `@RequestMapping`: Ruta base del controlador.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Métodos HTTP.
- `@RequestBody`, `@PathVariable`, `@RequestParam`: Manejo de parámetros.
- `@Autowired`: Inyección de dependencias.
- `@Service`: Marca clase de lógica de negocio.
- `@Transactional`: Manejo de transacciones.

---

## Base de datos

- `users` → Usuarios registrados.
- `categories` → Categorías de productos.
- `products` → Productos con precio, stock y categoría.
- `orders` → Pedidos de usuarios.
- `order_items` → Ítems de cada pedido (producto + cantidad).
- `carts` / `cart_items` → Carrito temporal del usuario.
- `payments` → Pago asociado a un pedido (`@OneToOne`).

---

## Ejemplo de uso (REST)

### Crear usuario
```http
POST /users
{
  "firstName": "Valentín",
  "lastName": "Igarzabal",
  "email": "valen@example.com",
  "password": "secret123"
}
```

### Crear producto
```http
POST /products
{
  "name": "Notebook Lenovo",
  "description": "Ryzen 5, 16GB RAM",
  "price": 1200.0,
  "stock": 10,
  "categoryId": 1
}
```

### Crear orden
```http
POST /orders
{
  "userId": 1,
  "items": [
    { "productId": 1, "quantity": 2 }
  ]
}
```

### Pagar orden
```http
POST /payments
{
  "orderId": 1,
  "amount": 2400.0,
  "method": "CARD"
}
```

---

## Notas técnicas

- En MySQL evitamos tablas `order`, `user`, `count` porque son palabras reservadas.  
  → Usamos `orders`, `users`, `items_count`.  
- `spring.jpa.open-in-view` está activado por defecto → ojo con LazyInitializationException si lo desactivás.  
- Controllers devuelven entidades directamente → podrías usar **DTOs de salida** (`UserResponse`, `OrderResponse`) para no exponer `password` ni relaciones enteras.
