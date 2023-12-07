package com.colphacy.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReplyAdminListViewDTO {
    private Long id;
    private String content;
    private LocalDateTime createdTime;
    private Long employeeId;
    private String employeeName;
}
