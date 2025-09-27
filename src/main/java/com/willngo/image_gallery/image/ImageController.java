package com.willngo.image_gallery.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            path = "{imageId}/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadImage(@RequestParam("file")MultipartFile file,
                            @RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description) {
        imageService.uploadImage(file, title, description);
    }

    @GetMapping(path = "{imageId}/image/download", produces = MediaType.ALL_VALUE)
    public byte[] downloadImage(@PathVariable("imageId") UUID imageId) {
        return imageService.downloadImage(String.valueOf(imageId));
    }
}
