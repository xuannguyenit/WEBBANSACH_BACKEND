package com.xuannguyen.identity.dto.response.fileresponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResourceResponse {

    String id;

    String name;

    String type;

    Long size;

    String url;
}
