package com.gongkademy.infra.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3FileService implements FileService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        //파일 비었는지 체크
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw new AmazonS3Exception("파일이 비어있습니다");
        }
        //확장자명 체크
        this.validateExtension(file.getOriginalFilename());

        //성공로직
        return this.uploadFileToS3(file);
    }


    private void validateExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new AmazonS3Exception("확장자명에 문제가 있습니다");
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extension)) {
            throw new AmazonS3Exception("확장자명에 문제가 있습니다");
        }
    }

    private String uploadFileToS3(MultipartFile file) {
        // 실제 업로드
        String originalFilename = file.getOriginalFilename(); // 원본 파일명
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자명
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; // 변경된 파일 명

        try (InputStream is = file.getInputStream();
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(is))) {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + extension);
            metadata.setContentLength(byteArrayInputStream.available());

            // S3에 파일 올리기
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);
            return amazonS3.getUrl(bucketName, s3FileName).toString();

        } catch (Exception e) {
            log.error("putObject 도중 에러: " + e.getMessage(), e);
            throw new AmazonS3Exception("에러");
        }
    }


    public void deleteFile(String fileAddress) {
        //s3 삭제
        String key = getKeyFromImageAddress(fileAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new AmazonS3Exception("에러");
        }
    }

    private String getKeyFromImageAddress(String fileAddress) {
        try {
            URL url = new URL(fileAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new AmazonS3Exception("에러");
        }
    }
}
