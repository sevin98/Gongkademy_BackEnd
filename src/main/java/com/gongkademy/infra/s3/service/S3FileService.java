package com.gongkademy.infra.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
    
    public byte[] downloadFile(String fileAddress) {
        String key = getKeyFromImageAddress(fileAddress);
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucketName, key));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        try {
			byte[] bytes = IOUtils.toByteArray(objectInputStream);
            return bytes;
		} catch (IOException e) {
            log.error("download 도중 에러: " + e.getMessage(), e);
            throw new AmazonS3Exception("에러");
		}
    }
    public String getdownloadFileName(String fileAddress){
        String[] addrSegments = fileAddress.split("/");
        return addrSegments[addrSegments.length - 1];
    }
    public String getFileUrl(String filename) {
        return amazonS3.getUrl(bucketName, filename).toString();
    }


    public String uploadProfileFile(MultipartFile file, String folderName) {
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw new AmazonS3Exception("파일 없음");
        }
        this.validateExtension(file.getOriginalFilename());
        return this.uploadProfileImageToS3(file, folderName);
    }

    private String uploadProfileImageToS3(MultipartFile file, String folderName) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String s3FileName = folderName + "/" + UUID.randomUUID().toString().substring(0, 10) + originalFilename;

        try (InputStream inputStream = file.getInputStream();
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream))) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/" + extension);
            objectMetadata.setContentLength(byteArrayInputStream.available());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
            return amazonS3.getUrl(bucketName, s3FileName).toString();
        } catch (Exception e) {
            log.error("putObject 도중 에러 : " + e.getMessage().toString());
            throw new AmazonS3Exception("에러");
        }
    }
}
