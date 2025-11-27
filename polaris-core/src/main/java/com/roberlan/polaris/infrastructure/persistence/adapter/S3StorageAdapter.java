package com.roberlan.polaris.infrastructure.persistence.adapter;

import com.roberlan.polaris.core.usecase.ports.out.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Service
public class S3StorageAdapter implements FileStoragePort {
    
    private final S3Client s3Client;
    
    @Value("${AWS_BUCKET_NAME}") private String bucketName;
    
    @Value("${AWS_S3_ENDPOINT}") private String endpoint;
    
    @Value("${AWS_S3_PUBLIC_ENDPOINT}") private String publicEndpoint;
    
    public S3StorageAdapter(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    @Override
    public String upload(String fileName, InputStream content, long size, String contentType) {
        String uniqueFileName = UUID.randomUUID() + "_" + fileName;
        
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(uniqueFileName).contentType(
                contentType).build();
        
        s3Client.putObject(request, RequestBody.fromInputStream(content, size));
        
        return publicEndpoint + "/" + bucketName + "/" + uniqueFileName;
    }
}