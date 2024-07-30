package com.gongkademy.infra.s3;

import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.infra.s3.service.FileCateg;
import com.gongkademy.infra.s3.service.S3FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Log4j2
@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    S3FileService s3FileService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestPart(value="categ") String categStr) {
        FileCateg categ = Arrays.stream(FileCateg.values())
                .filter(c -> c.name().equals(categStr))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_FILECATEG));

        String saveFile = s3FileService.uploadFile(file, categ);
        return ResponseEntity.ok(saveFile);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam("address") String addr){
        s3FileService.deleteFile(addr);
        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity<?> downloadFile(@RequestParam("address") String addr){
        log.info("download=====================");
        byte[] bytes = s3FileService.downloadFile(addr);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String fileName = s3FileService.getdownloadFileName(addr);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        log.info("download 완료");
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }
}
