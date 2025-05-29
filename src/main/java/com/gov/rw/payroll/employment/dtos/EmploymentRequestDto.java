package com.gov.rw.payroll.employment.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import com.gov.rw.payroll.employment.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EmploymentRequestDto(
        @NotBlank(message = "Department is required")
        String department,

        @NotBlank(message = "Position is required")
        String position,

        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        @NotNull(message = "Base salary is required")
        @Positive(message = "Base salary must be positive")
        double baseSalary,

        Status status,

        LocalDateTime joiningDate
) {
}