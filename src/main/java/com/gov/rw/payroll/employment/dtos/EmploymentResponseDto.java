package com.gov.rw.payroll.employment.dtos;

import lombok.Builder;
import com.gov.rw.payroll.employment.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EmploymentResponseDto(
        UUID id,
        String code,
        UUID employeeId,
        String department,
        String position,
        double baseSalary,
        Status status,
        LocalDateTime joiningDate
) {
}