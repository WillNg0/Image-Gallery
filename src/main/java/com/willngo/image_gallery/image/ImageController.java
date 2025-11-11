package com.willngo.image_gallery.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/image-gallery")
@CrossOrigin("*") //* enables all requests
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    //allows us to assign an image to imageId
    @PostMapping(
            path = "image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadImage(@RequestParam("file")MultipartFile file,
                            @RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description
                            ) {
        imageService.uploadImage(file, title, description);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Image> getAllImages() {
        return imageService.getAllImages();
    }

    @GetMapping(path = "{imageKey}/image/download", produces = MediaType.ALL_VALUE)
    public byte[] downloadImage(@PathVariable("imageKey") String s3Key) {
        return imageService.downloadImage(s3Key);
    }

    @PutMapping(path = "{imageKey}/title")
    public void editTitle(@PathVariable("imageKey") String imageKey,
                           @RequestParam("title") String title) {
        imageService.editTitle(title, imageKey);
    }

    @PutMapping(path = "{imageKey}/description")
    public void editDescription(@PathVariable("imageKey") String imageKey,
                                 @RequestParam("description") String description) {
        imageService.editDescription(description, imageKey);
    }

    @DeleteMapping(path="{imageKey}")
    public void deleteImage(@PathVariable("imageKey") String imageKey) {
        imageService.deleteImage(imageKey);
    }
 }
