package com.willngo.image_gallery.bucket;

public enum BucketName {

    PROFILE_IMAGE("willngo-image-gallery-upload");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
