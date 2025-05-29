package com.gov.rw.payroll.employment;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.gov.rw.payroll.commons.exceptions.BadRequestException;
import com.gov.rw.payroll.commons.exceptions.ResourceNotFoundException;
import com.gov.rw.payroll.employees.EmployeeRepository;
import com.gov.rw.payroll.employment.dtos.EmploymentRequestDto;
import com.gov.rw.payroll.employment.dtos.EmploymentResponseDto;
import com.gov.rw.payroll.employment.mappers.EmploymentMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmploymentService {
    private static final Logger log = LoggerFactory.getLogger(EmploymentService.class);
    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentMapper employmentMapper;

    public EmploymentResponseDto createEmployment(EmploymentRequestDto dto) {
        // Verify that the employee exists
        if (!employeeRepository.existsById(dto.employeeId())) {
            throw new ResourceNotFoundException("Employee", "id", dto.employeeId());
        }

        Employment employment = employmentMapper.toEntity(dto);
        employmentRepository.save(employment);
        log.info("Employment created for employee ID: {}", dto.employeeId());
        return employmentMapper.toResponseDto(employment);
    }

    public EmploymentResponseDto getEmploymentById(UUID id) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment", "id", id));
        return employmentMapper.toResponseDto(employment);
    }

    public List<EmploymentResponseDto> getEmploymentsByEmployeeId(UUID employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", "id", employeeId);
        }
        return employmentRepository.findByEmployeeId(employeeId).stream()
                .map(employmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<EmploymentResponseDto> getAllEmployments() {
        return employmentRepository.findAll().stream()
                .map(employmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public EmploymentResponseDto updateEmploymentStatus(UUID id, Status status) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment", "id", id));
        employment.setStatus(status);
        employmentRepository.save(employment);
        log.info("Employment status updated for ID: {}", id);
        return employmentMapper.toResponseDto(employment);
    }

    public EmploymentResponseDto updateEmploymentBaseSalary(UUID id, double baseSalary) {
        if (baseSalary <= 0) {
            throw new BadRequestException("Base salary must be positive");
        }
        
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment", "id", id));
        employment.setBaseSalary(baseSalary);
        employmentRepository.save(employment);
        log.info("Employment base salary updated for ID: {}", id);
        return employmentMapper.toResponseDto(employment);
    }

    public void deleteEmployment(UUID id) {
        if (!employmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employment", "id", id);
        }
        employmentRepository.deleteById(id);
        log.info("Employment deleted with ID: {}", id);
    }
}