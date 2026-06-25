package com.xuannguyen.identity.kafka.consumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuannguyen.event.dto.NotificationEvent;
import com.xuannguyen.identity.entity.Notification;
import com.xuannguyen.identity.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderNotificationConsumer {

    NotificationRepository notificationRepository;

    ObjectMapper objectMapper;

    @KafkaListener(
            topics = "notification-order",
            groupId = "identity-order-notification-group"
    )
    public void listen(NotificationEvent event) {
        log.info("Received order notification event: {}", event);

        if (event == null) {
            log.warn("Order notification event is null");
            return;
        }

        if (event.getRecipient() == null || event.getRecipient().trim().isEmpty()) {
            log.warn("Order notification recipient is empty");
            return;
        }

        Notification notification = Notification.builder()
                .channel(event.getChannel())
                .recipient(event.getRecipient())
                .templateCode(event.getTemplateCode())
                .subject(event.getSubject())
                .body(event.getBody())
                .paramJson(convertParamToJson(event))
                .read(false)
                .readAt(null)
                .enable(true)
                .build();

        notificationRepository.save(notification);

        log.info("Saved order notification. recipient={}", event.getRecipient());
    }

    private String convertParamToJson(NotificationEvent event) {
        if (event.getParam() == null || event.getParam().isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(event.getParam());
        } catch (JsonProcessingException exception) {
            log.warn("Cannot convert order notification param to json", exception);
            return null;
        }
    }
}
