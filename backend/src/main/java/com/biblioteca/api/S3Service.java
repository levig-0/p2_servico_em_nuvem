package com.biblioteca.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class S3Service {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.s3.bucket.uploads:biblioteca-uploads-grupo-2026}")
    private String uploadsBucket;

    public Map<String, String> gerarPresignedUrlUpload(Integer livroId, String contentType) {
        if (contentType == null || contentType.isBlank()) {
            contentType = "image/jpeg";
        }

        String extensao = ".jpg";
        if (contentType.equalsIgnoreCase("image/png")) {
            extensao = ".png";
        } else if (contentType.equalsIgnoreCase("image/webp")) {
            extensao = ".webp";
        }

        String objectKey = "capas/livro-" + livroId + extensao;

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(awsRegion))
                .build()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(uploadsBucket)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            Map<String, String> resposta = new HashMap<>();
            resposta.put("uploadUrl", presignedRequest.url().toString());
            resposta.put("bucket", uploadsBucket);
            resposta.put("objectKey", objectKey);
            resposta.put("contentType", contentType);

            return resposta;
        }
    }
}
