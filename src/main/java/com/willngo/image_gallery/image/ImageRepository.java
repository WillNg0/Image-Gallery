package com.willngo.image_gallery.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findByS3Key(String s3Key);

    @Override
    boolean existsById(UUID id);

    boolean existsByS3Key(String s3Key);

    List<Image> findByOriginalFileNameContainIgnoreCase(String fileName);

}
