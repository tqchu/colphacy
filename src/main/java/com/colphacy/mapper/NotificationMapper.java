package com.colphacy.mapper;


import com.colphacy.dto.notification.NotificationDTO;
import com.colphacy.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDTO notificationToNotificationDTO(Notification notification);
}
