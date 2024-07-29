package com.gongkademy.infra.s3;

import com.gongkademy.infra.s3.service.FileCateg;
import com.gongkademy.infra.s3.service.S3FileService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;

@Log4j2
@RestController
@RequestMapping("api/v1")
public class S3TestController {
    @Autowired
    S3FileService s3ImageService;
    @PostMapping("/s3/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image) {
        String profileImage = s3ImageService.uploadFile(image, FileCateg.PROFILE);
        return ResponseEntity.ok(profileImage);
    }

    @DeleteMapping("/s3/delete")
    public ResponseEntity<?> s3delete(@RequestParam String addr){
        s3ImageService.deleteFile(addr);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/s3/download")
    public ResponseEntity<?> s3download(@RequestParam("address") String addr){
        log.info("download=====================");
        byte[] bytes = s3ImageService.downloadFile(addr);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String fileName = s3ImageService.getdownloadFileName(addr);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        log.info("download 완료");
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }
}
