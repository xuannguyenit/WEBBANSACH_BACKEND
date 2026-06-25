package com.xuannguyen.identity.dto.request.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacebookPostProductsRequest {
    /**
     * ID cấu hình fanpage trong DB.
     * Nếu null thì service lấy fanpage active của user.
     */
    private String facebookPageConfigId;

    /**
     * Danh sách sản phẩm cần đăng.
     */
    private List<String> productIds;

    /**
     * URL website frontend.
     * Ví dụ: http://localhost:3000 hoặc https://your-domain.com
     */
    private String websiteBaseUrl;

    /**
     * URL backend public để Facebook đọc ảnh.
     * Ví dụ khi local dùng ngrok: https://xxx.ngrok-free.app/identity
     */
    private String publicBackendBaseUrl;
}
