package com.xuannguyen.identity.controller.image;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageDataResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.Image;
import com.xuannguyen.identity.service.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/upload-file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file ảnh lên server ")
    public ApiResponse<ImageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        return imageService.uploadFile(file);
    }

    @GetMapping("/{id}/info")
    @Operation(summary = "Lấy thông tin ảnh")
    public ApiResponse<ImageResponse> getInfo(@PathVariable String id) {
        return imageService.getInfo(id);
    }

    @GetMapping("/{id}/data")
    @Operation(summary = "Lấy dữ liệu ảnh dạng base64")
    public ApiResponse<ImageDataResponse> getImageData(@PathVariable String id) {
        return imageService.getImageData(id);
    }

    @GetMapping("/{id}/view")
    @Operation(summary = "Xem ảnh trực tiếp theo id")
    public ResponseEntity<byte[]> viewImage(@PathVariable String id) {
        Image image = imageService.getImageById(id);

        return ResponseEntity.ok()
                .contentType(getMediaType(image.getType()))
                .body(image.getData());
    }
    private MediaType getMediaType(String type) {
        if (type == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        return switch (type.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "svg" -> MediaType.valueOf("image/svg+xml");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
