package com.xuannguyen.identity.service.image.impl;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageDataResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.Image;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.ImageRepository;
import com.xuannguyen.identity.service.image.ImageService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "png", "jpg", "jpeg", "gif", "svg"
    );

    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024; // 5MB

    @Override
    public ApiResponse<ImageResponse> uploadFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        try {
            Image image = Image.builder()
                    .name(originalFilename)
                    .type(extension)
                    .size(file.getSize())
                    .data(file.getBytes())
                    .build();

            Image savedImage = imageRepository.save(image);

            return ApiResponse.<ImageResponse>builder()
                    .message("Upload image successfully")
                    .result(toImageResponse(savedImage))
                    .build();

        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_NOT_UPLOAD);
        }
    }

    @Override
    public ApiResponse<ImageResponse> getInfo(String id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));

        return ApiResponse.<ImageResponse>builder()
                .message("Get image info successfully")
                .result(toImageResponse(image))
                .build();
    }

    @Override
    public ApiResponse<ImageDataResponse> getImageData(String id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));

        String base64 = Base64.getEncoder().encodeToString(image.getData());

        String dataUrl = "data:" + getMimeType(image.getType()) + ";base64," + base64;

        ImageDataResponse response = ImageDataResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .size(image.getSize())
                .base64(base64)
                .dataUrl(dataUrl)
                .build();

        return ApiResponse.<ImageDataResponse>builder()
                .message("Get image data successfully")
                .result(response)
                .build();
    }

    private ImageResponse toImageResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .size(image.getSize())
                .url("/images/" + image.getId() + "/data")
                .build();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        if (!originalFilename.contains(".")) {
            throw new AppException(ErrorCode.FILE_NOT_SUPPORT);
        }

        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new AppException(ErrorCode.FILE_NOT_SUPPORT);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String getMimeType(String type) {
        if (type == null) {
            return "application/octet-stream";
        }

        return switch (type.toLowerCase()) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }
    @Override
    public Image getImageById(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
    }

}
