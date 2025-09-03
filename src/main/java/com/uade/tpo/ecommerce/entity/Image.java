package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image_table")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ⚠chadGTP recomendacion: Mejor usar byte[] con @Lob en vez de java.sql.Blob
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] image;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    private Product product;
}


