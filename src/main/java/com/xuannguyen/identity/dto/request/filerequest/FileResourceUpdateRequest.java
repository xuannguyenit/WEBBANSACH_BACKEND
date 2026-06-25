package com.xuannguyen.identity.dto.request.filerequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResourceUpdateRequest {
    String name;
}
