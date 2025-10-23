package com.willngo.image_gallery.image;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column()
    private String imageFileLink;

    @Column(nullable = false)
    private String imageKey; //S3 key

    @Column()
    private String title;

    @Column()
    private String description;

    public Image() {}

    //parameterized constructor for creating new images
    public Image(String imageFileLink, String imageKey, String title, String description) {
        this.imageFileLink = imageFileLink;
        this.imageKey = imageKey;
        this.description = description;
        this.title = title;
    }

    public Image(String imageFileLink, String imageKey) {
        this.imageFileLink = imageFileLink;
        this.imageKey = imageKey;
        this.description = "";
        this.title = "";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public Optional<String> getImageFileLink() {
        return Optional.ofNullable(imageFileLink); //if pfp is null
    }

    public void setImageFileLink(String imageFileLink) {
        this.imageFileLink = imageFileLink;
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Optional<String> getImageKey() {
        return Optional.ofNullable(imageKey);
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }


    //generate equals and hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image that = (Image) o;
        return Objects.equals(id, that.id)
                && Objects.equals(imageFileLink, that.imageFileLink)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageFileLink, title, description);
    }
}
