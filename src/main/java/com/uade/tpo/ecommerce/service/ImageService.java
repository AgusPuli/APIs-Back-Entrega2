package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.controllers.image.AddFileRequest;
import com.uade.tpo.ecommerce.entity.Image;

public interface ImageService {
    // Crea o reemplaza la imagen de un producto (1:1)
    Image create(Long productId, AddFileRequest request);

    // Busca por id de imagen
    Image viewById(long id);

    // (Útil) Obtiene imagen por productId
    Image viewByProductId(long productId);

    // (Útil) Elimina imagen por productId
    void deleteByProductId(long productId);
}
