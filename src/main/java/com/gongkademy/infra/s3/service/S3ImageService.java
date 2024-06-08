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
public class S3ImageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile image)  {
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new AmazonS3Exception("파일이 비어있습니다");
        }
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image){
        //확장자명 체크
        this.validateImageFileExtention(image.getOriginalFilename());

        //성공하면 업로드
        try{
            return this.uploadImageToS3(image);
        }catch (IOException e){
            throw new AmazonS3Exception("에러");
        }
    }

    private void validateImageFileExtention(String filename){
        //확장자명 체크
        int lastDotIndex = filename.lastIndexOf(".");
        if(lastDotIndex == -1){
            throw new AmazonS3Exception("확장자명에 문제가 있습니다");
        }

        String extention = filename.substring(lastDotIndex +1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg","jpeg","png","gif");

        if(!allowedExtentionList.contains(extention)){
            throw new AmazonS3Exception("확장자명에 문제가 있습니다");
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException{

        //실제업로드

        String originalFilename = image.getOriginalFilename(); //원본 파일명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자명


        String s3FileName = UUID.randomUUID().toString().substring(0,10) + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        log.info("설명:-----------------");
        log.info("1" + originalFilename);
        log.info("2" + extention);
        log.info("3" + s3FileName);
        log.info("4" + is);
        log.info("5" + bytes);
        log.info("6" + metadata);
        log.info("7" + byteArrayInputStream);






        try{
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            log.info("putObject: " + putObjectRequest.toString());
            log.info("amazonS3: " + amazonS3);
            amazonS3.putObject(putObjectRequest); //put image to S3
        }catch (Exception e){
            log.error("putObject 도중 에러: "+ e.getMessage());
            throw new AmazonS3Exception("에러");
        }finally {
            byteArrayInputStream.close();
            is.close();
        }
        return amazonS3.getUrl(bucketName,s3FileName).toString();
    }
    public void deleteImageFromS3(String imageAddress){
        //s3 삭제
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName,key));
        }catch(Exception e){
            throw new AmazonS3Exception("에러");
        }
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1);
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new AmazonS3Exception("에러");
        }
    }
}
