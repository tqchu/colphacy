package com.colphacy.service.impl;

import com.colphacy.dto.notification.NotificationDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.NotificationMapper;
import com.colphacy.model.Notification;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.NotificationRepository;
import com.colphacy.service.NotificationService;
import com.colphacy.util.PageResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Sinks.Many<NotificationDTO> sink = Sinks.many().multicast().directBestEffort();

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

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

    @Override
    public PageResponse<NotificationDTO> list(Long employeeId, int offset, Integer limit, Boolean isRead) {
        int pageNo = offset / limit;

        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("createdTime").descending());

        Page<Notification> notificationPage;

        if (isRead != null) {
            notificationPage = notificationRepository.findByEmployeeIdAndRead(employeeId, isRead, pageable);
        } else {;
            notificationPage = notificationRepository.findByEmployeeId(employeeId, pageable);
        }

        Page<NotificationDTO> notificationDTOPage = notificationPage.map(notification -> notificationMapper.notificationToNotificationDTO(notification));

        return PageResponseUtils.getPageResponse(offset, notificationDTOPage);
    }

    @Override
    public void markTheNotificationAsRead(Long employeeId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndEmployeeId(notificationId, employeeId).orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy thông báo"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllNotificationAsRead(Long employeeId) {
        notificationRepository.markAllNotificationAsRead(employeeId);
    }
}
