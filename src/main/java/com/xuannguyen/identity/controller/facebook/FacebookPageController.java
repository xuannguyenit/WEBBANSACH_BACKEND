package com.xuannguyen.identity.controller.facebook;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.facebook.FacebookPageRequest;
import com.xuannguyen.identity.dto.request.facebook.FacebookPostProductsRequest;
import com.xuannguyen.identity.dto.response.facebook.FacebookPageResponse;
import com.xuannguyen.identity.dto.response.facebook.FacebookPostProductResponse;
import com.xuannguyen.identity.service.facebookservice.FacebookPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facebook-pages")
@RequiredArgsConstructor
public class FacebookPageController {

    private final FacebookPageService facebookPageService;

    @PostMapping
    public ApiResponse<FacebookPageResponse> saveFacebookPage(
            @RequestBody FacebookPageRequest request
    ) {
        return ApiResponse.<FacebookPageResponse>builder()
                .message("Lưu cấu hình fanpage thành công.")
                .result(facebookPageService.saveFacebookPage(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<FacebookPageResponse>> getMyFacebookPages() {
        return ApiResponse.<List<FacebookPageResponse>>builder()
                .message("Lấy danh sách fanpage thành công.")
                .result(facebookPageService.getMyFacebookPages())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFacebookPage(@PathVariable String id) {
        facebookPageService.deleteFacebookPage(id);

        return ApiResponse.<Void>builder()
                .message("Xóa cấu hình fanpage thành công.")
                .build();
    }

    @PostMapping("/post-products")
    public ApiResponse<List<FacebookPostProductResponse>> postProductsToFacebookPage(
            @RequestBody FacebookPostProductsRequest request
    ) {
        return ApiResponse.<List<FacebookPostProductResponse>>builder()
                .message("Đăng sản phẩm lên fanpage hoàn tất.")
                .result(facebookPageService.postProductsToFacebookPage(request))
                .build();
    }
}