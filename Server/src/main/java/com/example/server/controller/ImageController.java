package com.example.server.controller;

import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.ImageResponseDTO;
import com.example.server.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins ="http://localhost:4200")
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService service;

    public ImageController(ImageService service) {
        this.service = service;
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageResponseDTO>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cropId", required = false) Long cropId,
            @RequestParam(value = "trackerId", required = false) Long trackerId,
            @RequestParam("userId") Long userId) {

        ImageResponseDTO response = service.uploadImage(file, description, cropId, trackerId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ImageResponseDTO>success("Image uploaded successfully", response));
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ApiResponse<ImageResponseDTO>> getImageById(@PathVariable Long imageId) {
        ImageResponseDTO response = service.getImageById(imageId);
        return ResponseEntity.ok(ApiResponse.<ImageResponseDTO>success(response));
    }

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<ApiResponse<List<ImageResponseDTO>>> getImagesByCrop(@PathVariable Long cropId) {
        List<ImageResponseDTO> responses = service.getImagesByCrop(cropId);
        return ResponseEntity.ok(ApiResponse.<List<ImageResponseDTO>>success(responses));
    }

    @GetMapping("/tracker/{trackerId}")
    public ResponseEntity<ApiResponse<List<ImageResponseDTO>>> getImagesByTracker(@PathVariable Long trackerId) {
        List<ImageResponseDTO> responses = service.getImagesByTracker(trackerId);
        return ResponseEntity.ok(ApiResponse.<List<ImageResponseDTO>>success(responses));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ImageResponseDTO>>> getImagesByUser(@PathVariable Long userId) {
        List<ImageResponseDTO> responses = service.getImagesByUser(userId);
        return ResponseEntity.ok(ApiResponse.<List<ImageResponseDTO>>success(responses));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ImageResponseDTO>>> getAllImages() {
        List<ImageResponseDTO> responses = service.getAllImages();
        return ResponseEntity.ok(ApiResponse.<List<ImageResponseDTO>>success(responses));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        service.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.<Void>success("Image deleted successfully", null));
    }

    @GetMapping("/file/{storedName}")
    public ResponseEntity<byte[]> getImageFile(@PathVariable String storedName) {
        byte[] imageBytes = service.getImageBytes(storedName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }
}
