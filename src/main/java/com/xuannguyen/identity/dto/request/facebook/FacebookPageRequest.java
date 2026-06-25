package com.xuannguyen.identity.dto.request.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookPageRequest {
    private String pageId;

    private String pageName;

    private String pageAccessToken;

    private Boolean active;
}
