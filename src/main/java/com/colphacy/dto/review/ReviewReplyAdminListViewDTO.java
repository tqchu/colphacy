package com.colphacy.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReplyAdminListViewDTO {
    private Long id;
    private String content;
    private ZonedDateTime createdTime;
    private Long employeeId;
    private String employeeName;
}