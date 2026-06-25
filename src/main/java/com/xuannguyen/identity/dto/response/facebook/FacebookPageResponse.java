package com.xuannguyen.identity.dto.response.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookPageResponse {
    private String id;

    private Long userId;

    private String pageId;

    private String pageName;

    private Boolean active;
}
