package com.colphacy.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<String> upload(MultipartFile[] images);
}
