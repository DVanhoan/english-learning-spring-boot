package elearningspringboot.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file);

    // Đổi tên tham số 'fileName' thành 'publicId' để rõ ràng hơn
    boolean deleteFile(String publicId, String resourceType);

    String uploadBytes(byte[] data, String filename, String contentType);
    String uploadVideo(MultipartFile file);
    String uploadRawFile(MultipartFile file);
}