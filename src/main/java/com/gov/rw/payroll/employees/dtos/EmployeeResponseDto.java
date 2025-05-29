package com.gov.rw.payroll.employees.dtos;

import lombok.Builder;
import com.gov.rw.payroll.employees.Role;
import com.gov.rw.payroll.employees.Status;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record EmployeeResponseDto(
        UUID id,
        String code,
        String firstName,
        String lastName,
        String email,
        String mobile,
        Role roles,
        Status status,
        LocalDate dob
) {
}