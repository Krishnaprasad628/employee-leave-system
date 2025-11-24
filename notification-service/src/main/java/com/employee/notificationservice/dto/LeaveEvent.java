package com.employee.notificationservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveEvent {
    private String eventType;
    private Long employeeId;

    private LocalDate startDate;
    private LocalDate endDate;

    private long totalDays;
    private long approvedAt;
}

