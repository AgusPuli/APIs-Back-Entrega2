package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.controllers.products.ProductRequest;
import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.CategoryType;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product create(ProductRequest request) {
        // Validar que la categoría sea obligatoria
        if (request.getCategory() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría es obligatoria"
            );
        }

        // Buscar la categoría por su nombre (enum)
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Categoría no encontrada: " + request.getCategory()
                ));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .active(true)
                .build();
        return repository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado con id: " + id
                ));
    }

    @Override
    public Page<Product> listProducts(Pageable pageable) {
        // Solo productos activos para el catálogo público
        return repository.findAll(pageable);
    }

    @Override
    public Product update(Long id, ProductRequest request) {
        Product product = getProductById(id);

        // Actualizar categoría si viene en el request
        if (request.getCategory() != null) {
            Category category = categoryRepository.findByName(request.getCategory())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Categoría no encontrada: " + request.getCategory()
                    ));
            product.setCategory(category);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return repository.save(product);
    }

    @Override
    @Transactional
    public Map<String, Object> deleteProduct(Long id) {
        Product product = getProductById(id);
        Map<String, Object> response = new HashMap<>();

        // Verificar si tiene órdenes asociadas
        boolean hasOrders = !product.getOrderItems().isEmpty();

        if (hasOrders) {
            // SOFT DELETE: Solo desactivar (preserva historial de órdenes)
            product.setActive(false);
            repository.save(product);

            response.put("message", "Producto desactivado correctamente (tiene órdenes asociadas)");
            response.put("softDelete", true);
            response.put("productId", id);
        } else {
            // HARD DELETE: Eliminar físicamente (sin órdenes asociadas)
            repository.delete(product);

            response.put("message", "Producto eliminado permanentemente");
            response.put("softDelete", false);
            response.put("productId", id);
        }

        return response;
    }

    @Override
    public List<Product> findByCategory(CategoryType category) {
        // Solo productos activos de esta categoría
        return repository.findByCategoryNameAndActiveTrue(category);
    }
    @Override
    @Transactional
    public Product setActive(Long id, boolean active) {
        Product p = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Si tu campo es Boolean en vez de boolean, esto evita NPE
        boolean current = (p.getActive() instanceof Boolean)
                ? Boolean.TRUE.equals(p.getActive())
                : p.getActive(); // si es primitivo boolean

        if (current == active) {
            // No hay cambios; devolvemos como está
            return p;
        }

        p.setActive(active);
        // Si tu entidad mapea active como 0/1 en DB, el setter boolean->int ya debería estar resuelto por JPA/Hibernate o por tu mapeo.
        return repository.save(p);
    }
}