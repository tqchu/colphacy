package com.colphacy.controller;


import com.colphacy.dto.notification.NotificationDTO;
import com.colphacy.model.Employee;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.security.Principal;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmployeeService employeeService;

    private final Integer defaultPageSize;

    public NotificationController(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @GetMapping("/admin")
    public Flux<ServerSentEvent<NotificationDTO>> streamEvents(Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);

        return notificationService.getNotificationFlux(employee.getId());
    }

    @Operation(summary = "List notification", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping
    public PageResponse<NotificationDTO> list(Principal principal,
                                              @RequestParam(required = false, defaultValue = "0")
                                              @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                              @RequestParam(required = false)
                                                  @Size(min = 1, message = "Số lượng giới hạn phải lớn hơn 0") Integer limit) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        return notificationService.list(employee.getId(), offset, limit);
    }

    @Operation(summary = "Mark the notification as read", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("read/{id}")
    public void markTheNotificationAsRead(Principal principal, @PathVariable("id") Long notificationId) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        notificationService.markTheNotificationAsRead(employee.getId(), notificationId);
    }

    @Operation(summary = "Mark all notifications as read", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("read")
    public void markAllNotificationAsRead(Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        notificationService.markAllNotificationAsRead(employee.getId());
    }
}
