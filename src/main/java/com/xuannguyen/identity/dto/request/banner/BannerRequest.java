package com.xuannguyen.identity.dto.request.banner;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerRequest {

    String title;

    String description;

    String redirectUrl;

    Integer sortOrder;

    Boolean active;

    LocalDateTime startDate;

    LocalDateTime endDate;

    String imageId;
}