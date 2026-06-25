package com.xuannguyen.identity.service.notification;

import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.notification.NotificationResponse;

public interface NotificationService {
    PageResponse<NotificationResponse> getMyNotifications(
            int page,
            int size,
            Boolean read
    );

    Long countMyUnreadNotifications();

    NotificationResponse markAsRead(String id);

    void markAllAsRead();

    void delete(String id);
}
