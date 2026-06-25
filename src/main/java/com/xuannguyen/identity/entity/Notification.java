package com.xuannguyen.identity.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Kênh thông báo.
     * Ví dụ: EMAIL, SYSTEM, ORDER, PROMOTION.
     */
    @Column(length = 50)
    private String channel;

    /**
     * Người nhận.
     * Với EMAIL thì recipient là email.
     */
    @Column(nullable = false, length = 255)
    private String recipient;

    /**
     * Mã template nếu có.
     */
    @Column(name = "template_code", length = 100)
    private String templateCode;

    /**
     * Tiêu đề thông báo.
     */
    @Column(nullable = false, length = 255)
    private String subject;

    /**
     * Nội dung thông báo.
     */
    @Column(columnDefinition = "TEXT")
    private String body;

    /**
     * Dữ liệu param dạng JSON string.
     * Vì NotificationEvent.param là Map nên khi lưu DB ta convert sang JSON string.
     */
    @Column(name = "param_json", columnDefinition = "TEXT")
    private String paramJson;

    /**
     * Trạng thái đã đọc hay chưa.
     */
    @Column(name = "is_read", nullable = false)
    private Boolean read;

    /**
     * Thời điểm đọc.
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * Trạng thái bản ghi.
     */
    @Column(nullable = false)
    private Boolean enable;
}
