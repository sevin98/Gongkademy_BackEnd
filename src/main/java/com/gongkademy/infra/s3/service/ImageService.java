package com.gongkademy.infra.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public String upload(MultipartFile image);

}
