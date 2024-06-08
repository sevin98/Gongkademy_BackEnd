package com.gongkademy.infra.s3;

import com.gongkademy.infra.s3.service.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1")
public class S3TestController {
    @Autowired
    S3FileService s3ImageService;
    @PostMapping("/s3/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
        String profileImage = s3ImageService.uploadFile(image);
        return ResponseEntity.ok(profileImage);
    }


    @DeleteMapping("/s3/delete")
    public ResponseEntity<?> s3delete(@RequestParam String addr){
        s3ImageService.deleteFile(addr);
        return ResponseEntity.ok(null);
    }
}
