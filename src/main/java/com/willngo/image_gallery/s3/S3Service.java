package com.willngo.image_gallery.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class S3Service {
    private final S3Client s3Client;

    @Autowired
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void save(String bucketName, String key,
                          Optional<Map<String, String>> optionalMetadata,
                          InputStream inputStream) {
        try {
            // Build PutObjectRequest
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key);

            optionalMetadata.ifPresent(map -> {
                if (!map.isEmpty()) {
                    requestBuilder.metadata(map);
                }
            });

            PutObjectRequest putObjectRequest = requestBuilder.build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while storing file", e);
        }
    }

    public byte[] getObject(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            ResponseInputStream<GetObjectResponse> resBytes = s3Client.getObject(getObjectRequest);
            return resBytes.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading image from S3", e);
        }
    }
}
