package com.uade.tpo.ecommerce.controllers.image;

import com.uade.tpo.ecommerce.entity.Image;
import com.uade.tpo.ecommerce.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/products/{productId}/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // Subir/Reemplazar (multipart con @ModelAttribute para mapear AddFileRequest)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponse> upload(@PathVariable Long productId,
                                                @ModelAttribute AddFileRequest request) {
        Image saved = imageService.create(productId, request);
        String base64 = Base64.getEncoder().encodeToString(saved.getImage());

        ImageResponse resp = ImageResponse.builder()
                .id(saved.getId())
                .file(base64) // si preferís, podés devolver una URL en vez del base64
                .build();

        return ResponseEntity.ok(resp);
    }

    // Descargar como binario (útil para <img src>)
    @GetMapping("/raw")
    public ResponseEntity<byte[]> downloadRaw(@PathVariable Long productId) {
        Image img = imageService.viewByProductId(productId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image_" + productId + ".jpg\"")
                .contentType(MediaType.IMAGE_JPEG) // si querés soportar varios tipos, guarda contentType en la entidad
                .body(img.getImage());
    }

    // Obtener como base64 (compatible con tu ImageResponse)
    @GetMapping
    public ResponseEntity<ImageResponse> downloadBase64(@PathVariable Long productId) {
        Image img = imageService.viewByProductId(productId);
        String base64 = Base64.getEncoder().encodeToString(img.getImage());
        return ResponseEntity.ok(ImageResponse.builder()
                .id(img.getId())
                .file(base64)
                .build());
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        imageService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
