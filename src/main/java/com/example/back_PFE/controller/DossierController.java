package com.example.back_PFE.controller;

import com.example.back_PFE.services.ClientService;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dossiers")
public class DossierController {

    private final MinioClient minioClient;
    private final ClientService clientService;

    public DossierController(MinioClient minioClient, ClientService clientService) {
        this.minioClient = minioClient;
        this.clientService = clientService;
    }

    private String getBucketName(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID manquant");
        }
        return "client-" + clientId;  // chaque client a un bucket unique
    }

    // 📥 Ajouter ou modifier un fichier PDF
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(
            @RequestParam Long clientId,
            @RequestParam("file") MultipartFile file) {

        try {
            String bucketName = getBucketName(clientId);
            String fileName = file.getOriginalFilename();

            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType("application/pdf")
                            .build()
            );

            return ResponseEntity.ok("Fichier ajouté ou modifié avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }

    // 📄 Lister les fichiers d’un client
    @GetMapping("/list")
    public ResponseEntity<?> listClientFiles(@RequestParam Long clientId) {
        try {
            String bucketName = getBucketName(clientId);

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true)
                            .build()
            );

            List<String> files = new ArrayList<>();
            for (Result<Item> result : results) {
                files.add(result.get().objectName());
            }

            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }

    // ⬇️ Télécharger un fichier (par l'admin)
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam Long clientId, @RequestParam String fileName) {
        try {
            String bucketName = getBucketName(clientId);

            InputStream fileStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(fileStream));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }

    // ❌ Supprimer un fichier
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam Long clientId, @RequestParam String fileName) {
        try {
            String bucketName = getBucketName(clientId);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            return ResponseEntity.ok("Fichier supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }
}

