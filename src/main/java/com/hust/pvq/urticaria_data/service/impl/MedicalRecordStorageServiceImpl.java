package com.hust.pvq.urticaria_data.service.impl;

import com.hust.pvq.urticaria_data.service.MedicalRecordStorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Service for storing medical record files in MinIO using a virtual folder structure.
 * <p>
 * Files are saved under the path: {@code userId/recordType/userId-recordType-currentTime.extension}.
 * A presigned URL is generated for secure download access, valid for a configurable period.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MedicalRecordStorageServiceImpl implements MedicalRecordStorageService {

    @Value("${minio.internal.endpoint}")
    private String internalEndpoint;

    @Value("${minio.public.endpoint}")
    private String publicEndpoint;

    private final MinioClient minioClient;

    /**
     * Name of the MinIO bucket for medical records, configurable via application.properties.
     */
    @Value("${minio.bucket.name:medical-record}")
    private String bucketName;

    /**
     * Uploads a medical record file to MinIO under virtual folders and returns a presigned URL.
     *
     * @param userId     Unique user identifier (first-level folder)
     * @param recordType Medical record category (second-level folder)
     * @param timestamp  Client-side timestamp for filename uniqueness (e.g., System.currentTimeMillis())
     * @param extension  File extension without the dot (e.g., "jpg", "pdf")
     * @param content    Byte content of the file to upload
     * @return Public URL (base, without query parameters) valid for downloading the file
     * @throws Exception Propagates IO and MinIO exceptions
     */
    @Override
    public String uploadMedicalRecordFile(
            String userId,
            String recordType,
            String timestamp,
            String extension,
            byte[] content
    ) throws Exception {
        // Build filename with extension
        String fileName = String.format("%s-%s-%s.%s", userId, recordType, timestamp, extension);
        // Build object key to simulate folders in MinIO
        String objectKey = String.join("/", userId, recordType, fileName);

        // Upload the file; MinIO auto-creates virtual folders based on prefix
        try (InputStream stream = new ByteArrayInputStream(content)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(stream, content.length, -1)
                            .build()
            );
        }

        // Generate a presigned GET URL valid for 7 days
        String getPresignedObjectUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectKey)
                        .expiry(7, TimeUnit.DAYS)
                        .build()
        );

        // Return url of medical record file
        String baseUrl = getPresignedObjectUrl.split("\\?")[0];
        String path = baseUrl.substring(internalEndpoint.length());
        return publicEndpoint + path;
    }

}
