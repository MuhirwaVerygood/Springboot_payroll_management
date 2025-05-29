package com.gov.rw.payroll.deductions.dtos;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeductionsResponseDto(
        UUID id,
        String code,
        String deductionName,
        double percentage
) {
}