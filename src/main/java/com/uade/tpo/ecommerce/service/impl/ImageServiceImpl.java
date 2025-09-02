package com.uade.tpo.ecommerce.service.impl;
import com.uade.tpo.ecommerce.entity.Image;
import com.uade.tpo.ecommerce.repository.ImageRepository;
import com.uade.tpo.ecommerce.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Image create(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image viewById(long id) {
        return imageRepository.findById(id).get();
    }
}
