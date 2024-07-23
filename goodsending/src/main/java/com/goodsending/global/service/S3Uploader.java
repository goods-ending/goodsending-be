package com.goodsending.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class S3Uploader {

  private final AmazonS3 amazonS3;
  private final String bucket;

  public S3Uploader(AmazonS3 amazonS3, @Value("${S3_BUCKET_NAME}") String bucket) {
    this.amazonS3 = amazonS3;
    this.bucket = bucket;
  }

  public String upload(MultipartFile multipartFile, String dirName) throws IOException {
    // 원본 파일 명
    String originalFileName = multipartFile.getOriginalFilename();
    log.info("파일 업로드 : {}", originalFileName);

    // UUID를 파일명에 추가
    String uuid = UUID.randomUUID().toString();
    String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_"); // " " -> "_"

    String fileName = dirName + "/" + uniqueFileName; // 업로드할 파일 경로 설정
    File uploadFile = convert(multipartFile, uniqueFileName); // S3가 처리할 수 있는 File 로 변경

    String uploadImageUrl = putS3(uploadFile, fileName); // s3에 업로드
    removeNewFile(uploadFile); // 로컬에 저장된 file 삭제
    return uploadImageUrl;
  }

  private File convert(MultipartFile file, String uniqueFileName) throws IOException {
    File convertFile = new File(uniqueFileName);
    if (!convertFile.createNewFile()) { // 이미 존재하는 파일일 경우
      log.error("파일 생성 실패");
      throw CustomException.of(ExceptionCode.FILE_UPLOAD_FAILED);
    }

    // MultipartFile 데이터를 File 에 쓰기
    try (FileOutputStream fos = new FileOutputStream(convertFile)) {
      fos.write(file.getBytes());
    } catch (IOException e) {
      log.error("파일 처리 중 오류 발생 : {}", e.getMessage());
      throw CustomException.of(ExceptionCode.FILE_UPLOAD_FAILED);
    }

    return convertFile;
  }

  private String putS3(File uploadFile, String fileName) {
    // 업로드할 버킷, 파일명, 파일을 설정
    PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, uploadFile)
        .withCannedAcl(CannedAccessControlList.PublicRead);

    amazonS3.putObject(putObjectRequest); // S3 버킷에 업로드

    return amazonS3.getUrl(bucket, fileName).toString(); // 버킷에 업로드 된 파일 url
  }

  private void removeNewFile(File targetFile) {
    if (targetFile.delete()) {
      log.info("파일이 삭제되었습니다.");
    } else {
      log.info("파일이 삭제되지 못했습니다.");
    }
  }
}
