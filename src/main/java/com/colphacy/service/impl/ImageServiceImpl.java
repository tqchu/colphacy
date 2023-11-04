package com.colphacy.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.colphacy.exception.ImageUploadingException;
import com.colphacy.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${colphacy.api.azure.connection-string}")
    private String connectionString;

    @Value("${colphacy.api.azure.storage.container-name}")
    private String containerName;

    public List<String> upload(MultipartFile[] images) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                futures.add(uploadImageAsync(image, containerClient));
            }
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private CompletableFuture<String> uploadImageAsync(MultipartFile image, BlobContainerClient containerClient) {
        return CompletableFuture.supplyAsync(() -> {
            String uniqueFileName = image.getOriginalFilename() + "_" + UUID.randomUUID();
            BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);
            BlobHttpHeaders headers = new BlobHttpHeaders();
            headers.setContentType("image/jpg");

            try {
                byte[] imageData = image.getBytes();
                InputStream imageStream = new ByteArrayInputStream(imageData);

                blobClient.uploadWithResponse(new BlobParallelUploadOptions(imageStream).setHeaders(headers), null, null);
                return blobClient.getBlobUrl();
            } catch (Exception e) {
                throw new ImageUploadingException("Failed to upload image to Azure Blob Storage");
            }
        });
    }

}
