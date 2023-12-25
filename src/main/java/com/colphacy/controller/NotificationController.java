package com.colphacy.controller;


import com.colphacy.model.Employee;
import com.colphacy.model.Notification;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.security.Principal;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmployeeService employeeService;
    @GetMapping("/admin")
    public Flux<ServerSentEvent<Notification>> streamEvents(Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);

        return notificationService.getNotificationFlux(employee.getId());
    }
}
