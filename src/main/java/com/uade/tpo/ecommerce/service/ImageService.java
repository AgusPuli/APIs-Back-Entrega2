package com.uade.tpo.ecommerce.service;


import com.uade.tpo.ecommerce.entity.Image;
import org.springframework.stereotype.Service;

@Service
public interface ImageService {
    public Image create(Image image);

    public Image viewById(long id);
}

