package com.gov.rw.payroll.employees.mappers;

import org.springframework.stereotype.Component;
import com.gov.rw.payroll.auth.dtos.RegisterRequestDto;
import com.gov.rw.payroll.employees.Employee;
import com.gov.rw.payroll.employees.Status;
import com.gov.rw.payroll.employees.dtos.EmployeeResponseDto;

import java.util.UUID;

@Component
public class EmployeeMapper {

    public Employee toEntity(RegisterRequestDto dto) {
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setCode(generateEmployeeCode());
        employee.setFirstName(dto.firstName());
        employee.setLastName(dto.lastName());
        employee.setEmail(dto.email());
        employee.setMobile(dto.mobile());
        employee.setPassword(dto.password());
        employee.setDob(dto.dob());
        employee.setStatus(Status.DISABLED); // Default status
        return employee;
    }

    public EmployeeResponseDto toResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .code(employee.getCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .mobile(employee.getMobile())
                .roles(employee.getRoles())
                .status(employee.getStatus())
                .dob(employee.getDob())
                .build();
    }

    public String generateEmployeeCode() {
        // Simple implementation - in a real system, this might be more sophisticated
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}