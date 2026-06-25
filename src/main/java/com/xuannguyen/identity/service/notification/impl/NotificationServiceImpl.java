package com.xuannguyen.identity.service.notification.impl;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.notification.NotificationResponse;
import com.xuannguyen.identity.entity.Notification;
import com.xuannguyen.identity.entity.User;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.NotificationRepository;
import com.xuannguyen.identity.repository.UserRepository;
import com.xuannguyen.identity.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;
    UserRepository userRepository;

    @Override
    public PageResponse<NotificationResponse> getMyNotifications(
            int page,
            int size,
            Boolean read
    ) {
        if (page < 1) {
            throw new RuntimeException("Page phải lớn hơn hoặc bằng 1");
        }

        if (size < 1) {
            throw new RuntimeException("Size phải lớn hơn hoặc bằng 1");
        }

        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Notification> notificationPage;

        if (read != null) {
            notificationPage = notificationRepository
                    .findByRecipientAndReadAndEnableTrueOrderByCreatedAtDesc(
                            currentUser.getEmail(),
                            read,
                            pageable
                    );
        } else {
            notificationPage = notificationRepository
                    .findByRecipientAndEnableTrueOrderByCreatedAtDesc(
                            currentUser.getEmail(),
                            pageable
                    );
        }

        List<NotificationResponse> responses = notificationPage
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<NotificationResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(notificationPage.getTotalPages())
                .totalElements(notificationPage.getTotalElements())
                .data(responses)
                .build();
    }

    @Override
    public Long countMyUnreadNotifications() {
        User currentUser = getCurrentUser();

        return notificationRepository.countByRecipientAndReadFalseAndEnableTrue(
                currentUser.getEmail()
        );
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(String id) {
        User currentUser = getCurrentUser();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thông báo không tồn tại"));

        if (!notification.getRecipient().equals(currentUser.getEmail())) {
            throw new RuntimeException("Bạn không có quyền đọc thông báo này");
        }

        if (Boolean.FALSE.equals(notification.getRead())) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        Notification savedNotification = notificationRepository.save(notification);

        return toResponse(savedNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(0, 1000);

        Page<Notification> notificationPage =
                notificationRepository.findByRecipientAndReadAndEnableTrueOrderByCreatedAtDesc(
                        currentUser.getEmail(),
                        false,
                        pageable
                );

        LocalDateTime now = LocalDateTime.now();

        notificationPage.getContent().forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(notificationPage.getContent());
    }

    @Override
    @Transactional
    public void delete(String id) {
        User currentUser = getCurrentUser();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thông báo không tồn tại"));

        if (!notification.getRecipient().equals(currentUser.getEmail())) {
            throw new RuntimeException("Bạn không có quyền xóa thông báo này");
        }

        notification.setEnable(false);

        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .channel(notification.getChannel())
                .recipient(notification.getRecipient())
                .templateCode(notification.getTemplateCode())
                .subject(notification.getSubject())
                .body(notification.getBody())
                .paramJson(notification.getParamJson())
                .read(notification.getRead())
                .readAt(notification.getReadAt())
                .enable(notification.getEnable())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}