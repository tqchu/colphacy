package com.colphacy.dto.notification;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class NotificationDTO {
    private Long id;

    private ZonedDateTime createdTime;

    private String url;

    private String title;

    private String description;

    private boolean read;

    private String image;
    private Long employeeId;
}
