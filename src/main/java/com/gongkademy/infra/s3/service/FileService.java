package com.gongkademy.infra.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public String uploadFile(MultipartFile image);
    public void deleteFile(String fileAddress);
}
