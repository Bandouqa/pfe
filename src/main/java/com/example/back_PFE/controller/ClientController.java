package com.example.back_PFE.controller;


import com.example.back_PFE.email.EmailServiceCandidat;
import com.example.back_PFE.entities.Candidature;
import com.example.back_PFE.entities.Client;
import com.example.back_PFE.entities.Statusdoss;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.repository.ClientFileDTO;
import com.example.back_PFE.repository.ClientRepo;
import com.example.back_PFE.services.ClientService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = "http://localhost:4200")
public class ClientController {

    private final ClientService clientService;
    private final MinioClient minioClient;
    private final JwtUtil jwtUtil;

    public ClientController(MinioClient minioClient , JwtUtil jwtUtil ,ClientService clientService) {
        this.minioClient = minioClient;
        this.jwtUtil = jwtUtil;
        this.clientService= clientService;
    }
    @Value("${minio.bucket-name}")
    private String bucketName; // Récupérer le nom du bucket depuis les propriétés

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestBody Client client, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            // Extraire le token JWT et obtenir l'email
            String token = authorizationHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);

            System.out.println("Email extrait : " + userId);

            Client newClient = clientService.addClient(client,userId);

            return new ResponseEntity<>(newClient, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/{userId}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable Long userId,
                                             @RequestParam(name = "files") List<MultipartFile> file) {
        try {

                if (file == null || file.isEmpty()) {
                    return ResponseEntity.badRequest().body("Fichier non fourni !");
                }

            // Vérifier si le bucket existe
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                // Si le bucket n'existe pas, le créer
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // Télécharger le fichier dans le bucket
            for (MultipartFile files : file) {
                String fileName = userId + "/" + System.currentTimeMillis() + "-" + files.getOriginalFilename();

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(files.getInputStream(), files.getSize(), -1)
                                .contentType(files.getContentType())
                                .build()
                );
            }
            return ResponseEntity.ok("Fichier téléchargé avec succès!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'upload du fichier: " + e.getMessage());
        }
    }
    private final String basePath = "uploads/clients";

    private String getClientFolderPath(Long userId) {
        return basePath + "/" + userId;
    }
   /* @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateFile(@PathVariable Long userId,
                                             @RequestParam("fileName") String fileName,
                                             @RequestParam("file") MultipartFile file) {
        try {
            // Construire le chemin de l'objet : ex "1/RihabTOUHAMIcv.pdf"
            String objectName = userId + "/" + fileName;

            // Vérifier si le fichier existe dans MinIO
            try {
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
            } catch (ErrorResponseException e) {
                if (e.errorResponse().code().equals("NoSuchKey")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Fichier non trouvé dans MinIO !");
                } else {
                    throw new RuntimeException("Erreur lors de la vérification du fichier", e);
                }
            }

            // Supprimer l'ancien fichier
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // Ajouter le nouveau fichier
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return ResponseEntity.ok("Fichier mis à jour avec succès dans MinIO !");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du fichier dans MinIO", e);
        }
    }*/


    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<String> deleteFile(@PathVariable Long userId,
                                             @RequestParam("fileName") String fileName) {
        try {
            String objectName = fileName; // exemple : "1/RihabTOUHAMIcv.pdf"

            // Vérifie que le fichier existe
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // Supprime le fichier
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            return ResponseEntity.ok("Fichier supprimé avec succès dans MinIO !");
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fichier non trouvé dans MinIO !");
            } else {
                throw new RuntimeException("Erreur MinIO : " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier dans MinIO", e);
        }
    }

    @GetMapping("/{userId}/list")
    public ResponseEntity<List<String>> listClientFiles(@PathVariable Long userId) {
        try {
            String prefix = userId + "/" ; // le dossier du client dans le bucket
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            List<String> files = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                files.add(item.objectName());
            }

            return ResponseEntity.ok(files);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des fichiers du client", e);
        }
    }


    @GetMapping("/{userId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long userId,
                                                            @RequestParam("fileName") String fileName) {
        try {
            String objectName = fileName; // Exemple : "1/CV.pdf"
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du téléchargement du fichier depuis MinIO", e);
        }
    }


    @GetMapping("/all-with-files")
    public ResponseEntity<List<ClientFileDTO>> getAllClientsWithFiles() {
        try {
            List<Client> clients = clientService.getAllClients(); // à adapter selon ta méthode de récupération
            List<ClientFileDTO> result = new ArrayList<>();

            for (Client client : clients) {
                String prefix = client.getUser().getId() + "/"; // répertoire client
                List<String> fichiers = new ArrayList<>();

                Iterable<Result<Item>> results = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .prefix(prefix)
                                .recursive(true)
                                .build()
                );

                for (Result<Item> resultItem : results) {
                    Item item = resultItem.get();
                    fichiers.add(item.objectName());
                }

                ClientFileDTO dto = new ClientFileDTO();
                dto.setUserId(client.getUser().getId());
                dto.setId_client(client.getId_client());
                dto.setNom(client.getNom());
                dto.setPrenom(client.getPrenom());
                dto.setEmail(client.getEmail());
                dto.setPhone(client.getPhone());
                dto.setSpecialite(client.getSpecialite());
                dto.setNiveau(client.getNiveau());
                dto.setAdress(client.getAdress());
                dto.setFichiers(fichiers);
                dto.setStatus(client.getStatus());
                result.add(dto);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des clients avec leurs fichiers", e);
        }
    }

    @PutMapping("/{id_client}/status")
    public ResponseEntity<Map<String, String>>  updateClientStatus(@PathVariable Long id_client, @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            clientService.updateClientStatus(id_client, Statusdoss.valueOf(newStatus));
            return ResponseEntity.ok(Map.of("message", "Statut mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    /*  @PutMapping("/update/{id_client}")
    public HttpStatus updateClient(@PathVariable("id_client") Long id_client, @RequestBody client updatedClient) {
        // Récupérer la candidature mise à jour
        clientService.updateClient(id_client, updatedClient);

        return HttpStatus.OK;
    }*/
    @GetMapping("/{id_client}/client")
    public ResponseEntity<Client> getClientById(@PathVariable("id_client") Long id_client) {
        Optional<Client> client = clientService.getClientById(id_client);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @Autowired
    private ClientRepo clientRepo;

    @GetMapping("/{userId}/isProfileCompleted")
    public ResponseEntity<Boolean> isProfileCompleted(@PathVariable Long userId) {
        Optional<Client> optionalClient = clientRepo.findByUserId(userId);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();

            boolean isCompleted =
                    client.getNom() != null && !client.getNom().isEmpty() &&
                            client.getPrenom() != null && !client.getPrenom().isEmpty() &&
                            client.getEmail() != null && !client.getEmail().isEmpty() &&
                            client.getPhone() != null && !client.getPhone().isEmpty() &&
                            client.getSpecialite() != null && !client.getSpecialite().isEmpty() &&
                            client.getNiveau() != null && !client.getNiveau().isEmpty() &&
                            client.getAdress() != null && !client.getAdress().isEmpty();

            return ResponseEntity.ok(isCompleted);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


