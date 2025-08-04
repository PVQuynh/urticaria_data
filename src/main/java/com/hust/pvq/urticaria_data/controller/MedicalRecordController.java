package com.hust.pvq.urticaria_data.controller;

import com.hust.pvq.urticaria_data.service.MedicalRecordStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling medical record uploads.
 * Defines clear API paths and uses consistent naming conventions.
 */
@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordStorageService storageService;

    /**
     * Uploads a medical record file for a given user and record type.
     * <p>
     * Endpoint: POST /api/v1/medical-records/upload
     * Consumes: multipart/form-data
     * </p>
     *
     * @param userId     identifier of the user
     * @param recordType type/category of the medical record
     * @param file       the MultipartFile to upload
     * @return 201 Created with URL to access the stored file
     * @throws Exception if upload fails
     */
    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadRecord(
            @RequestParam("user_id") String userId,
            @RequestParam("record_type") String recordType,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        // Extract extension and bytes
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        byte[] content = file.getBytes();
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Delegate to service
        String fileUrl = storageService.uploadMedicalRecordFile(
                userId,
                recordType,
                timestamp,
                extension,
                content
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileUrl);
    }
}
