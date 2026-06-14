package com.xuannguyen.identity.service.image;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageDataResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.Image;
import org.springframework.web.multipart.MultipartFile;
public interface ImageService {
    ApiResponse<ImageResponse> uploadFile(MultipartFile file);

    ApiResponse<ImageResponse> getInfo(String id);

    ApiResponse<ImageDataResponse> getImageData(String id);
    Image getImageById(String id);
}
