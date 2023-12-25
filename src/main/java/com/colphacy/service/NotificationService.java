package com.colphacy.service;

import com.colphacy.model.Notification;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface NotificationService {
    Flux<ServerSentEvent<Notification>> getNotificationFlux(Long employeeId);

    void publishNotification(Notification notification);
}
