package com.xuannguyen.identity.dto.response.facebook;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPostProductResponse {
    private String productId;

    private String productName;

    private String facebookPostId;

    private String status;

    private String message;

    private String errorMessage;
}
