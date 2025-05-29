package com.gov.rw.payroll.employment.mappers;

import org.springframework.stereotype.Component;
import com.gov.rw.payroll.employment.Employment;
import com.gov.rw.payroll.employment.Status;
import com.gov.rw.payroll.employment.dtos.EmploymentRequestDto;
import com.gov.rw.payroll.employment.dtos.EmploymentResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EmploymentMapper {

    public Employment toEntity(EmploymentRequestDto dto) {
        Employment employment = new Employment();
        employment.setId(UUID.randomUUID());
        employment.setCode(generateEmploymentCode());
        employment.setEmployeeId(dto.employeeId());
        employment.setDepartment(dto.department());
        employment.setPosition(dto.position());
        employment.setBaseSalary(dto.baseSalary());
        employment.setStatus(dto.status() != null ? dto.status() : Status.INACTIVE);
        employment.setJoiningDate(dto.joiningDate() != null ? dto.joiningDate() : LocalDateTime.now());
        return employment;
    }

    public EmploymentResponseDto toResponseDto(Employment employment) {
        return EmploymentResponseDto.builder()
                .id(employment.getId())
                .code(employment.getCode())
                .employeeId(employment.getEmployeeId())
                .department(employment.getDepartment())
                .position(employment.getPosition())
                .baseSalary(employment.getBaseSalary())
                .status(employment.getStatus())
                .joiningDate(employment.getJoiningDate())
                .build();
    }

    private String generateEmploymentCode() {
        // Simple implementation - in a real system, this might be more sophisticated
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}