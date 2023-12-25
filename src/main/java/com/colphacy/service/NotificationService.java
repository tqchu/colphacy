package com.colphacy.service;

import com.colphacy.dto.notification.NotificationDTO;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface NotificationService {
    Flux<ServerSentEvent<NotificationDTO>> getNotificationFlux(Long employeeId);

    void publishNotification(NotificationDTO notification);
}
