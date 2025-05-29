package com.gov.rw.payroll.paySlip.mappers;

import org.springframework.stereotype.Component;
import com.gov.rw.payroll.employees.Employee;
import com.gov.rw.payroll.employees.mappers.EmployeeMapper;
import com.gov.rw.payroll.paySlip.PaySlip;
import com.gov.rw.payroll.paySlip.Status;
import com.gov.rw.payroll.paySlip.dtos.PaySlipRequestDto;
import com.gov.rw.payroll.paySlip.dtos.PaySlipResponseDto;

import java.util.UUID;

@Component
public class PaySlipMapper {

    private final EmployeeMapper employeeMapper;

    public PaySlipMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public PaySlip toEntity(PaySlipRequestDto dto, Employee employee, double houseAmount, double transportAmount,
                           double employeeTaxedAmount, double pensionAmount, double medicalInsuranceAmount,
                           double otherTaxedAmount, double grossSalary, double netSalary, String department, String position) {
        PaySlip paySlip = new PaySlip();
        paySlip.setId(UUID.randomUUID());
        paySlip.setCode(generatePaySlipCode());
        paySlip.setEmployee(employee);
        paySlip.setDepartment(department);
        paySlip.setPosition(position);
        paySlip.setHouseAmount(houseAmount);
        paySlip.setTransportAmount(transportAmount);
        paySlip.setEmployeeTaxedAmount(employeeTaxedAmount);
        paySlip.setPensionAmount(pensionAmount);
        paySlip.setMedicalInsuranceAmount(medicalInsuranceAmount);
        paySlip.setOtherTaxedAmount(otherTaxedAmount);
        paySlip.setGrossSalary(grossSalary);
        paySlip.setNetSalary(netSalary);
        paySlip.setMonth(dto.month());
        paySlip.setYear(dto.year());
        paySlip.setStatus(Status.PENDING); // Default status
        return paySlip;
    }

    public PaySlipResponseDto toResponseDto(PaySlip paySlip) {
        return PaySlipResponseDto.builder()
                .id(paySlip.getId())
                .code(paySlip.getCode())
                .employee(employeeMapper.toResponseDto(paySlip.getEmployee()))
                .department(paySlip.getDepartment())
                .position(paySlip.getPosition())
                .houseAmount(paySlip.getHouseAmount())
                .transportAmount(paySlip.getTransportAmount())
                .employeeTaxedAmount(paySlip.getEmployeeTaxedAmount())
                .pensionAmount(paySlip.getPensionAmount())
                .medicalInsuranceAmount(paySlip.getMedicalInsuranceAmount())
                .otherTaxedAmount(paySlip.getOtherTaxedAmount())
                .grossSalary(paySlip.getGrossSalary())
                .netSalary(paySlip.getNetSalary())
                .month(paySlip.getMonth())
                .year(paySlip.getYear())
                .status(paySlip.getStatus())
                .build();
    }

    private String generatePaySlipCode() {
        // Simple implementation - in a real system, this might be more sophisticated
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}