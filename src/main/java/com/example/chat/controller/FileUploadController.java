package com.example.chat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // ── UPLOAD ──────────────────────────────────────────────────────────────
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uniqueFileName   = UUID.randomUUID().toString() + "_" + originalFileName;

            Path targetLocation = Paths.get(uploadDir).resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/download/")
                    .path(uniqueFileName)
                    .toUriString();

            response.put("fileName", originalFileName);
            response.put("fileUrl",  fileDownloadUri);

            return ResponseEntity.ok(response);

        } catch (IOException ex) {
            response.put("error", "Impossible de sauvegarder le fichier : " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ── DOWNLOAD (force téléchargement automatique pour tous les types) ──────
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            File file     = filePath.toFile();

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] fileBytes = Files.readAllBytes(filePath);

            // ✅ Extraire le nom original (après le UUID_)
            String originalName = filename.contains("_")
                    ? filename.substring(filename.indexOf("_") + 1)
                    : filename;

            return ResponseEntity.ok()
                    // ✅ octet-stream = téléchargement forcé pour TOUS les fichiers
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + originalName + "\"")
                    .body(fileBytes);

        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
