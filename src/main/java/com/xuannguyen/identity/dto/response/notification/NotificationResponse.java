package com.xuannguyen.identity.dto.response.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {

    String id;

    String channel;

    String recipient;

    String templateCode;

    String subject;

    String body;

    String paramJson;

    Boolean read;

    LocalDateTime readAt;

    Boolean enable;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
