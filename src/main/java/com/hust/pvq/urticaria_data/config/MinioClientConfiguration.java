package com.hust.pvq.urticaria_data.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for MinIO client.
 * <p>
 * Reads connection properties from application.properties / application.yml
 * and exposes a singleton MinioClient bean.
 * </p>
 */
@Configuration
public class MinioClientConfiguration {

    @Value("${minio.internal.endpoint}")
    private String internalEndpoint;

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String secretKey;

    /**
     * Creates and configures a MinioClient bean.
     *
     * @return a MinioClient instance ready to use
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(internalEndpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

}
