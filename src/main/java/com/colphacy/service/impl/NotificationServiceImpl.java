package com.colphacy.service.impl;

import com.colphacy.dto.notification.NotificationDTO;
import com.colphacy.service.NotificationService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Sinks.Many<NotificationDTO> sink = Sinks.many().multicast().directBestEffort();

    public Flux<ServerSentEvent<NotificationDTO>> getNotificationFlux(Long employeeId) {
        return sink.asFlux()
                .filter(notification -> notification.getEmployeeId().equals(employeeId))
                .map(notification -> ServerSentEvent.<NotificationDTO>builder()
                        .id(String.valueOf(notification.getId()))
                        .event("notifications")
                        .data(notification)
                        .build());
    }

    public void publishNotification(NotificationDTO notification) {
        sink.tryEmitNext(notification);
    }
}
