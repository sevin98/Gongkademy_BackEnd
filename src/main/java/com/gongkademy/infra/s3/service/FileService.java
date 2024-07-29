package com.gongkademy.infra.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    // - 파일 비었는지 검사
    public String uploadFile(MultipartFile image, FileCateg categ);
    public void deleteFile(String fileAddress);
    String getFileUrl(String fileName);
}
