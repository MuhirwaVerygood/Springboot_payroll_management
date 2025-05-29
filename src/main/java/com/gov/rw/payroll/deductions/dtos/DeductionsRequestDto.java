package com.gov.rw.payroll.deductions.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record DeductionsRequestDto(
        @NotBlank(message = "Deduction name is required")
        String deductionName,

        @NotNull(message = "Percentage is required")
        @Positive(message = "Percentage must be positive")
        double percentage
) {
}