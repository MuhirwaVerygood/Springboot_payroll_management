package com.gov.rw.payroll.paySlip.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaySlipRequestDto(
        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        @NotNull(message = "Month is required")
        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        int month,

        @NotNull(message = "Year is required")
        @Min(value = 2000, message = "Year must be valid")
        int year
) {
}