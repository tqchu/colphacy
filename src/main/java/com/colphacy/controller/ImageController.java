package com.colphacy.controller;

import com.colphacy.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images/")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public List<String> upload(@RequestParam("image") MultipartFile[] images) {
        return imageService.upload(images);
    }
}
