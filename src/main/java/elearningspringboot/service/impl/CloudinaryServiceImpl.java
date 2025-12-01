package elearningspringboot.service.impl;

import com.cloudinary.Cloudinary;
import elearningspringboot.enumeration.ErrorCode;
import elearningspringboot.exception.AppException;
import elearningspringboot.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements StorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        String publicId = UUID.randomUUID().toString();
        log.info("Uploading IMAGE file to Cloudinary: publicId={}", publicId);
        try {
            Map params = Map.of(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "image"
            );
            Map result = cloudinary.uploader().upload(file.getBytes(), params); // Dùng getBytes()
            String url = result.get("secure_url").toString();
            log.info("Upload successful: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Upload IMAGE file failed: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public boolean deleteFile(String publicId, String resourceType) {
        log.info("Deleting file from Cloudinary: publicId={}, resourceType={}", publicId, resourceType);
        try {
            Map params = Map.of("resource_type", resourceType);
            cloudinary.uploader().destroy(publicId, params);
            log.info("Delete successful");
            return true;
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String uploadBytes(byte[] data, String filename, String contentType) {
        String publicId = FilenameUtils.getBaseName(filename);
        log.info("Uploading bytes to Cloudinary: publicId={}, contentType={}", publicId, contentType);

        try {
            Map params = Map.of(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "image"
            );

            Map result = cloudinary.uploader().upload(data, params);

            String url = result.get("secure_url").toString();
            log.info("Upload bytes successful: {}", url);
            return url;

        } catch (Exception e) {
            log.error("Upload bytes failed: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public String uploadVideo(MultipartFile file) {
        String publicId = UUID.randomUUID().toString();
        log.info("Uploading video to Cloudinary: publicId={}", publicId);

        try {
            Map params = Map.of(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "video"
            );

//            Map result = cloudinary.uploader().upload(file.getInputStream(), params);
            Map result = cloudinary.uploader().upload(file.getBytes(), params);

            String url = result.get("secure_url").toString();
            log.info("Upload video successful: {}", url);
            return url;

        } catch (IOException e) {
            log.error("Upload video failed due to IOException: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        } catch (Exception e) {
            log.error("Upload video failed due to unexpected error: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public String uploadRawFile(MultipartFile file) {
        // Giữ lại tên file gốc cho dễ download
        String publicId = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        log.info("Uploading RAW file to Cloudinary: publicId={}", publicId);
        try {
            Map params = Map.of(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "raw" // Chỉ định là file "thô"
            );

            // Dùng getInputStream() cho nhanh
            Map result = cloudinary.uploader().upload(file.getBytes(), params);

            String url = result.get("secure_url").toString();
            log.info("Upload raw file successful: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Upload RAW file failed: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }
    }
}