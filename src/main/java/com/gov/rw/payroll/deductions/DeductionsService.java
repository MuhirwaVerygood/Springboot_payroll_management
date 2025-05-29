package com.gov.rw.payroll.deductions;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.gov.rw.payroll.commons.exceptions.BadRequestException;
import com.gov.rw.payroll.commons.exceptions.ResourceNotFoundException;
import com.gov.rw.payroll.deductions.dtos.DeductionsRequestDto;
import com.gov.rw.payroll.deductions.dtos.DeductionsResponseDto;
import com.gov.rw.payroll.deductions.mappers.DeductionsMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DeductionsService {
    private static final Logger log = LoggerFactory.getLogger(DeductionsService.class);
    private final DeductionsRepository deductionsRepository;
    private final DeductionsMapper deductionsMapper;

    public DeductionsResponseDto createDeduction(DeductionsRequestDto dto) {
        // Check if deduction with the same name already exists
        if (deductionsRepository.findByDeductionName(dto.deductionName()).isPresent()) {
            throw new BadRequestException("Deduction with name '" + dto.deductionName() + "' already exists");
        }

        // Validate percentage
        if (dto.percentage() <= 0 || dto.percentage() > 100) {
            throw new BadRequestException("Percentage must be between 0 and 100");
        }

        Deductions deduction = deductionsMapper.toEntity(dto);
        deductionsRepository.save(deduction);
        log.info("Deduction created: {}", dto.deductionName());
        return deductionsMapper.toResponseDto(deduction);
    }

    public DeductionsResponseDto getDeductionById(UUID id) {
        Deductions deduction = deductionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction", "id", id));
        return deductionsMapper.toResponseDto(deduction);
    }

    public DeductionsResponseDto getDeductionByName(String name) {
        Deductions deduction = deductionsRepository.findByDeductionName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction", "name", name));
        return deductionsMapper.toResponseDto(deduction);
    }

    public List<DeductionsResponseDto> getAllDeductions() {
        return deductionsRepository.findAll().stream()
                .map(deductionsMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public DeductionsResponseDto updateDeductionPercentage(UUID id, double percentage) {
        // Validate percentage
        if (percentage <= 0 || percentage > 100) {
            throw new BadRequestException("Percentage must be between 0 and 100");
        }

        Deductions deduction = deductionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction", "id", id));
        deduction.setPercentage(percentage);
        deductionsRepository.save(deduction);
        log.info("Deduction percentage updated for: {}", deduction.getDeductionName());
        return deductionsMapper.toResponseDto(deduction);
    }

    public void deleteDeduction(UUID id) {
        if (!deductionsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deduction", "id", id);
        }
        deductionsRepository.deleteById(id);
        log.info("Deduction deleted with ID: {}", id);
    }

    // Method to initialize default deductions if they don't exist
    public void initializeDefaultDeductions() {
        createDeductionIfNotExists("EmployeeTax", 30);
        createDeductionIfNotExists("Pension", 6); // Updated from 3% to 6% as per requirements
        createDeductionIfNotExists("MedicalInsurance", 5);
        createDeductionIfNotExists("Housing", 14);
        createDeductionIfNotExists("Transport", 14);
        createDeductionIfNotExists("Others", 5);
    }

    private void createDeductionIfNotExists(String name, double percentage) {
        if (deductionsRepository.findByDeductionName(name).isEmpty()) {
            DeductionsRequestDto dto = DeductionsRequestDto.builder()
                    .deductionName(name)
                    .percentage(percentage)
                    .build();
            createDeduction(dto);
            log.info("Default deduction created: {} with {}%", name, percentage);
        }
    }
}