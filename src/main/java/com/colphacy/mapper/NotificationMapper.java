package com.colphacy.mapper;


import com.colphacy.dto.notification.NotificationDTO;
import com.colphacy.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "employee.id", target = "employeeId")
    NotificationDTO notificationToNotificationDTO(Notification notification);
}
