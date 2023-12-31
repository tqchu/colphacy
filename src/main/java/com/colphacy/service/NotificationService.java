package com.colphacy.service;

import com.colphacy.dto.notification.NotificationDTO;
import com.colphacy.payload.response.PageResponse;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface NotificationService {
    Flux<ServerSentEvent<NotificationDTO>> getNotificationFlux(Long employeeId);

    void publishNotification(NotificationDTO notification);

    PageResponse<NotificationDTO> list(Long employeeId, int offset, Integer limit);

    void markTheNotificationAsRead(Long employeeId, Long notificationId);

    void markAllNotificationAsRead(Long employeeId);
}
