package com.willngo.image_gallery.image;

import com.willngo.image_gallery.bucket.BucketName;
import com.willngo.image_gallery.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.io.IOException;
import java.util.*;

//where all business logic happens
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Service fileStore;
    private final String bucketName;

    @Autowired
    public ImageService(ImageRepository imageRepository, S3Service fileStore) {
        this.imageRepository = imageRepository;
        this.fileStore = fileStore;
        this.bucketName = BucketName.PROFILE_IMAGE.getBucketName();

    }

    void uploadImage(MultipartFile file, String title, String description) {
        if(file.isEmpty()) {
            throw new IllegalStateException(("Cannot upload empty file"));
        }

        String contentType = file.getContentType();
        if(!contentType.startsWith("image/") || contentType == null) {
            throw new IllegalStateException("File must be an image");
        }

        String fileName = file.getOriginalFilename();
        if(fileName == null) {
            throw new IllegalArgumentException("Invalid file: Empty file");
        }

        //4. grab some metadata from file if any
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        //5. store the image in s3 and update database with s3 image link
        String originalFileName = file.getOriginalFilename();
        String key = String.format("%s-%s", originalFileName, UUID.randomUUID());
        try {
            fileStore.save(bucketName, key, Optional.of(metadata), file.getInputStream());
            // Store the complete key instead of just filename
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        String imageTitle = (title != null) ? title : "";
        String imageDescription = (title != null) ? description : "";
        Image image = new Image(key, imageTitle, imageDescription);

        imageRepository.addImage(image);
    }

    public Optional<Image> getImageById(UUID id) {
        return imageRepository.findById(id);
    }

    public Optional<Image> getImageByKey(String key) {
        return imageRepository.findByS3Key(key);
    }

    public byte[] downloadImage(String s3Key) {
//        // 1) Find the user by id
//        Image image = imageRepository.findByS3Key(s3Key)
//                .orElseThrow(() -> new IllegalArgumentException("Image not found with S3 key: " + s3Key));

        // 3) Fetch from S3 using the stored key
        return fileStore.getObject(bucketName, s3Key);
    }

    public List<Image> getAllImages() {
        return imageRepository.getAllImages();
    }

    public void editTitle(String title, String key) {
        imageRepository.editTitle(title, key);
    }

    public void editDescription(String description, String key) {
        imageRepository.editDescription(description, key);
    }
    //TODO: add a delete method
    //after implementing these -> update controller calss

}
