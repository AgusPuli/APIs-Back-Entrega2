package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar solo productos activos (para el catálogo público)
    Page<Product> findByActiveTrue(Pageable pageable);

    // Buscar por categoría solo activos
    List<Product> findByCategoryNameAndActiveTrue(CategoryType categoryName);

    //  Buscar por ID sin filtrar por active (para admin)
    Optional<Product> findById(Long id);

    // Obtener todos (incluyendo inactivos, para admin)
    Page<Product> findAll(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> lockById(@Param("id") Long id);
}
