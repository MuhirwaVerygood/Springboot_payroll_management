package com.gov.rw.payroll.paySlip.dtos;

import lombok.Builder;
import com.gov.rw.payroll.employees.dtos.EmployeeResponseDto;
import com.gov.rw.payroll.paySlip.Status;

import java.util.UUID;

@Builder
public record PaySlipResponseDto(
        UUID id,
        String code,
        EmployeeResponseDto employee,
        String department,
        String position,
        double houseAmount,
        double transportAmount,
        double employeeTaxedAmount,
        double pensionAmount,
        double medicalInsuranceAmount,
        double otherTaxedAmount,
        double grossSalary,
        double netSalary,
        int month,
        int year,
        Status status
) {
}