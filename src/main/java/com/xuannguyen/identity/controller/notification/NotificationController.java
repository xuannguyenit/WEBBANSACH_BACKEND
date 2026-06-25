package com.xuannguyen.identity.controller.notification;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.notification.NotificationResponse;
import com.xuannguyen.identity.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification", description = "API quản lý thông báo")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    NotificationService notificationService;

    @GetMapping("/my")
    @Operation(summary = "Lấy danh sách thông báo của user đang đăng nhập")
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean read
    ) {
        PageResponse<NotificationResponse> response =
                notificationService.getMyNotifications(page, size, read);

        return ApiResponse.<PageResponse<NotificationResponse>>builder()
                .code(200)
                .message("Lấy danh sách thông báo thành công")
                .result(response)
                .build();
    }

    @GetMapping("/my/unread-count")
    @Operation(summary = "Đếm số thông báo chưa đọc")
    public ApiResponse<Long> countMyUnreadNotifications() {
        Long count = notificationService.countMyUnreadNotifications();

        return ApiResponse.<Long>builder()
                .code(200)
                .message("Đếm thông báo chưa đọc thành công")
                .result(count)
                .build();
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu thông báo là đã đọc")
    public ApiResponse<NotificationResponse> markAsRead(
            @PathVariable String id
    ) {
        NotificationResponse response = notificationService.markAsRead(id);

        return ApiResponse.<NotificationResponse>builder()
                .code(200)
                .message("Đánh dấu đã đọc thành công")
                .result(response)
                .build();
    }

    @PatchMapping("/my/read-all")
    @Operation(summary = "Đánh dấu tất cả thông báo là đã đọc")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đánh dấu tất cả thông báo thành đã đọc thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm thông báo")
    public ApiResponse<Void> delete(
            @PathVariable String id
    ) {
        notificationService.delete(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa thông báo thành công")
                .build();
    }
}