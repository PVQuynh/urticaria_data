package com.hust.pvq.urticaria_data.controller;

import com.hust.pvq.urticaria_data.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UploadDataController {
    private final UploadService uploadService;

    @PostMapping(path = "/api/v1/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String upload(@RequestPart(value = "file", required = false) MultipartFile files) throws IOException {
        return uploadService.uploadFile(files.getOriginalFilename(), files.getBytes());
    }


}
