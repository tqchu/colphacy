package com.colphacy.service.impl;

import com.colphacy.model.Notification;
import com.colphacy.service.NotificationService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Sinks.Many<Notification> sink = Sinks.many().multicast().directBestEffort();

    public Flux<ServerSentEvent<Notification>> getNotificationFlux(Long employeeId) {
        return sink.asFlux()
                .filter(notification -> notification.getEmployee().getId().equals(employeeId))
                .map(notification -> ServerSentEvent.<Notification>builder()
                        .id(String.valueOf(notification.getId()))
                        .event("notifications")
                        .data(notification)
                        .build());
    }

    public void publishNotification(Notification notification) {
        sink.tryEmitNext(notification);
    }
}
