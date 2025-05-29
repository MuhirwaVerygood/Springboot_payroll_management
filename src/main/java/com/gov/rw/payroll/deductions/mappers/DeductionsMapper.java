package com.gov.rw.payroll.deductions.mappers;

import org.springframework.stereotype.Component;
import com.gov.rw.payroll.deductions.Deductions;
import com.gov.rw.payroll.deductions.dtos.DeductionsRequestDto;
import com.gov.rw.payroll.deductions.dtos.DeductionsResponseDto;

import java.util.UUID;

@Component
public class DeductionsMapper {

    public Deductions toEntity(DeductionsRequestDto dto) {
        Deductions deductions = new Deductions();
        deductions.setId(UUID.randomUUID());
        deductions.setCode(generateDeductionCode());
        deductions.setDeductionName(dto.deductionName());
        deductions.setPercentage(dto.percentage());
        return deductions;
    }

    public DeductionsResponseDto toResponseDto(Deductions deductions) {
        return DeductionsResponseDto.builder()
                .id(deductions.getId())
                .code(deductions.getCode())
                .deductionName(deductions.getDeductionName())
                .percentage(deductions.getPercentage())
                .build();
    }

    private String generateDeductionCode() {
        // Simple implementation - in a real system, this might be more sophisticated
        return "DED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}