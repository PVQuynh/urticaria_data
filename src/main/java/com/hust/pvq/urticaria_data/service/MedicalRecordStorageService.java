package com.hust.pvq.urticaria_data.service;

public interface MedicalRecordStorageService {

    String uploadMedicalRecordFile(String userId, String recordType, String timestamp, String extension, byte[] content) throws Exception;

}
