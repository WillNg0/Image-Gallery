package com.willngo.image_gallery.profile;

import com.willngo.image_gallery.bucket.BucketName;
import com.willngo.image_gallery.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

//where all business logic happens
@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final S3Service fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, S3Service fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles () {
        return userProfileDataAccessService.getUserProfiles();
    }

    void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        if(file.isEmpty()) {
            throw new IllegalStateException(("Cannot upload empty file"));
        }

        String contentType = file.getContentType();
        if(!contentType.startsWith("image/")) {
            throw new IllegalStateException("File must be an image");
        }
        //check whether user exists in our database //todo: replace with real database
        UserProfile user = userProfileDataAccessService.getUserProfiles().stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User %s not found", userProfileId)));
        //4. grab some metadata from file if any
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        //5. store the image in s3 and update database (userProfileImageLink) with s3 image link
        String bucketName = BucketName.PROFILE_IMAGE.getBucketName();
        String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        String key = String.format("%s/%s", user.getUserProfileId(), filename);
        try {
            fileStore.save(bucketName, key, Optional.of(metadata), file.getInputStream());
            user.setUserProfileImageLink(key); // Store the complete key instead of just filename
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    public byte[] downloadUserProfileImage(UUID userProfileId) {
        // 1) Find the user by id
        UserProfile user = userProfileDataAccessService.getUserProfiles().stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 2) Ensure user has an image link
        String key = user.getUserProfileImageLink()
                .orElseThrow(() -> new IllegalStateException("User has no profile image"));

        // 3) Fetch from S3 using the stored key
        String bucketName = BucketName.PROFILE_IMAGE.getBucketName();
        return fileStore.getObject(bucketName, key);
    }
}
