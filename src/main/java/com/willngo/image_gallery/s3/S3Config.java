package com.willngo.image_gallery.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String awsRegion;

    //class gives S3 client
    @Bean
    public S3Client s3Client() {
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.builder()
                .build();
        
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
