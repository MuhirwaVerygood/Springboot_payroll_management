package com.gov.rw.payroll.paySlip;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.gov.rw.payroll.commons.exceptions.BadRequestException;
import com.gov.rw.payroll.commons.exceptions.ResourceNotFoundException;
import com.gov.rw.payroll.deductions.Deductions;
import com.gov.rw.payroll.deductions.DeductionsRepository;
import com.gov.rw.payroll.employees.Employee;
import com.gov.rw.payroll.employees.EmployeeRepository;
import com.gov.rw.payroll.employment.Employment;
import com.gov.rw.payroll.employment.EmploymentRepository;
import com.gov.rw.payroll.employment.Status;
import com.gov.rw.payroll.paySlip.dtos.PaySlipRequestDto;
import com.gov.rw.payroll.paySlip.dtos.PaySlipResponseDto;
import com.gov.rw.payroll.paySlip.mappers.PaySlipMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaySlipService {
    private static final Logger log = LoggerFactory.getLogger(PaySlipService.class);
    private final PaySlipRepository paySlipRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionsRepository deductionsRepository;
    private final PaySlipMapper paySlipMapper;

    public PaySlipResponseDto generatePaySlip(PaySlipRequestDto dto) {
        // Check if employee exists
        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", dto.employeeId()));

        // Check if employee has active employment
        List<Employment> activeEmployments = employmentRepository.findByEmployeeId(dto.employeeId()).stream()
                .filter(e -> e.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());

        if (activeEmployments.isEmpty()) {
            throw new BadRequestException("Employee does not have any active employment");
        }

        // Use the most recent active employment
        Employment employment = activeEmployments.get(0); // Assuming the first one is the most recent

        // Check if pay slip for this employee, month, and year already exists
        if (paySlipRepository.findByEmployeeAndMonthAndYear(employee, dto.month(), dto.year()).isPresent()) {
            throw new BadRequestException("Pay slip already exists for this employee, month, and year");
        }

        // Get all deductions
        List<Deductions> allDeductions = deductionsRepository.findAll();
        if (allDeductions.isEmpty()) {
            throw new BadRequestException("No deductions found in the system");
        }

        // Create a map of deduction name to percentage
        Map<String, Double> deductionPercentages = new HashMap<>();
        for (Deductions deduction : allDeductions) {
            deductionPercentages.put(deduction.getDeductionName(), deduction.getPercentage());
        }

        // Calculate amounts
        double baseSalary = employment.getBaseSalary();
        double housingPercentage = deductionPercentages.getOrDefault("Housing", 14.0);
        double transportPercentage = deductionPercentages.getOrDefault("Transport", 14.0);
        double employeeTaxPercentage = deductionPercentages.getOrDefault("EmployeeTax", 30.0);
        double pensionPercentage = deductionPercentages.getOrDefault("Pension", 6.0);
        double medicalInsurancePercentage = deductionPercentages.getOrDefault("MedicalInsurance", 5.0);
        double otherPercentage = deductionPercentages.getOrDefault("Others", 5.0);

        double houseAmount = (baseSalary * housingPercentage) / 100;
        double transportAmount = (baseSalary * transportPercentage) / 100;
        double grossSalary = baseSalary + houseAmount + transportAmount;

        double employeeTaxedAmount = (baseSalary * employeeTaxPercentage) / 100;
        double pensionAmount = (baseSalary * pensionPercentage) / 100;
        double medicalInsuranceAmount = (baseSalary * medicalInsurancePercentage) / 100;
        double otherTaxedAmount = (baseSalary * otherPercentage) / 100;

        double totalDeductions = employeeTaxedAmount + pensionAmount + medicalInsuranceAmount + otherTaxedAmount;
        double netSalary = grossSalary - totalDeductions;

        // Create pay slip
        PaySlip paySlip = paySlipMapper.toEntity(
                dto, employee, houseAmount, transportAmount, employeeTaxedAmount, pensionAmount,
                medicalInsuranceAmount, otherTaxedAmount, grossSalary, netSalary,
                employment.getDepartment(), employment.getPosition()
        );

        paySlipRepository.save(paySlip);
        log.info("Pay slip generated for employee: {}, month: {}, year: {}", employee.getEmail(), dto.month(), dto.year());
        return paySlipMapper.toResponseDto(paySlip);
    }

    public PaySlipResponseDto getPaySlipById(UUID id) {
        PaySlip paySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaySlip", "id", id));
        return paySlipMapper.toResponseDto(paySlip);
    }

    public List<PaySlipResponseDto> getPaySlipsByEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        return paySlipRepository.findByEmployee(employee).stream()
                .map(paySlipMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PaySlipResponseDto> getPaySlipsByMonthAndYear(int month, int year) {
        return paySlipRepository.findByMonthAndYear(month, year).stream()
                .map(paySlipMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PaySlipResponseDto> getAllPaySlips() {
        return paySlipRepository.findAll().stream()
                .map(paySlipMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PaySlipResponseDto approvePaySlip(UUID id) {
        PaySlip paySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaySlip", "id", id));
        
        if (paySlip.getStatus() == com.gov.rw.payroll.paySlip.Status.PAID) {
            throw new BadRequestException("Pay slip is already approved");
        }
        
        paySlip.setStatus(com.gov.rw.payroll.paySlip.Status.PAID);
        paySlipRepository.save(paySlip);
        log.info("Pay slip approved for employee: {}, month: {}, year: {}", 
                paySlip.getEmployee().getEmail(), paySlip.getMonth(), paySlip.getYear());
        
        // Here we would trigger the email notification
        // This would be implemented with a database trigger or a message queue
        
        return paySlipMapper.toResponseDto(paySlip);
    }

    public List<PaySlipResponseDto> approveAllPendingPaySlips() {
        List<PaySlip> pendingPaySlips = paySlipRepository.findByStatus(com.gov.rw.payroll.paySlip.Status.PENDING);
        
        for (PaySlip paySlip : pendingPaySlips) {
            paySlip.setStatus(com.gov.rw.payroll.paySlip.Status.PAID);
            paySlipRepository.save(paySlip);
            log.info("Pay slip approved for employee: {}, month: {}, year: {}", 
                    paySlip.getEmployee().getEmail(), paySlip.getMonth(), paySlip.getYear());
            
            // Here we would trigger the email notification for each pay slip
        }
        
        return pendingPaySlips.stream()
                .map(paySlipMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public void deletePaySlip(UUID id) {
        if (!paySlipRepository.existsById(id)) {
            throw new ResourceNotFoundException("PaySlip", "id", id);
        }
        paySlipRepository.deleteById(id);
        log.info("Pay slip deleted with ID: {}", id);
    }
}