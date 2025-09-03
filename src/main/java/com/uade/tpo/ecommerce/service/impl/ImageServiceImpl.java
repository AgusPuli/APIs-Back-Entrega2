package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.image.AddFileRequest;
import com.uade.tpo.ecommerce.entity.Image;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.repository.ImageRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository images;
    private final ProductRepository products;

    @Override
    public Image create(Long productId, AddFileRequest request) {
        if (productId == null || request == null) {
            throw new IllegalArgumentException("productId y request son requeridos");
        }
        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es requerido");
        }

        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe id=" + productId));

        try {
            Image img = images.findByProductId(productId)
                    .orElseGet(() -> Image.builder().product(product).build());

            img.setImage(file.getBytes()); // tu entidad usa byte[] image
            // Si tu entidad Image tiene campo "name" o "filename", podés setearlo acá:
            // img.setName(request.getName());

            return images.save(img);
        } catch (Exception e) {
            throw new RuntimeException("Error guardando imagen: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Image viewById(long id) {
        return images.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Image viewByProductId(long productId) {
        return images.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no tiene imagen id=" + productId));
    }

    @Override
    public void deleteByProductId(long productId) {
        images.deleteByProductId(productId);
    }
}
