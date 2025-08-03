package com.hust.pvq.urticaria_data.service.Impl;


import com.hust.pvq.urticaria_data.service.UploadService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Data
public class UploadFileServiceImpl implements UploadService {

    private final MinioClient minioClient;
    private final ApplicationEventPublisher publisher;

    String defaultBucketName = "urticaria-data";

    @Override
    public String uploadFile(String name, byte[] content) {
        try {
            InputStream inputStream = new ByteArrayInputStream(content);

            // Tải lên tệp tin lên Minio sử dụng InputStream
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(name)
                            .stream(inputStream, content.length, -1) // Sử dụng InputStream
                            .build()
            );

            // Tạo pre-signed URL cho tệp tin vừa tải lên
            String url =
                    minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(defaultBucketName)
                                    .object(name)
                                    .expiry(7, TimeUnit.DAYS)
                                    .build());

            return url.split("\\?")[0];
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
