package com.uade.tpo.ecommerce.controllers.image;

import com.uade.tpo.ecommerce.entity.Image;
import com.uade.tpo.ecommerce.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@RestController
@RequestMapping("/products") // 👈 La ruta base coincide con tu frontend
@CrossOrigin(origins = "http://localhost:5173")
public class ImageController {

    @Autowired
    private ImageService imageService;

    // ✅ 1. MÉTODO POST: Para SUBIR la imagen (Este te faltaba)
    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponse> upload(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file // 👈 Recibimos el archivo directo
    ) {
        System.out.println("📸 Intentando subir imagen para Producto ID: " + productId);

        try {
            // Creamos el request manual para el servicio
            AddFileRequest request = new AddFileRequest();
            request.setFile(file);

            Image saved = imageService.create(productId, request);
            String base64 = Base64.getEncoder().encodeToString(saved.getImage());

            ImageResponse resp = ImageResponse.builder()
                    .id(saved.getId())
                    .file(base64)
                    .build();

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            System.err.println("❌ Error subiendo imagen: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ 2. MÉTODO GET: Para VER la imagen (Con protección anti-crash)
    @GetMapping("/{productId}/image/raw")
    public ResponseEntity<byte[]> downloadRaw(@PathVariable Long productId) {
        try {
            byte[] image = imageService.viewByProductId(productId).getImage();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image);

        } catch (Exception e) {
            // Si no hay imagen, devolvemos 404 silencioso para no romper el frontend
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ 3. MÉTODO DELETE (Opcional, por si borras productos)
    @DeleteMapping("/{productId}/image")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        try {
            imageService.deleteByProductId(productId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}