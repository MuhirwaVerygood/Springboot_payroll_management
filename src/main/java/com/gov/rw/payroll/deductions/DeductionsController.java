package com.gov.rw.payroll.deductions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.gov.rw.payroll.deductions.dtos.DeductionsRequestDto;
import com.gov.rw.payroll.deductions.dtos.DeductionsResponseDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deductions")
@AllArgsConstructor
@Tag(name = "Deductions Management", description = "APIs for managing deductions and tax rates")
public class DeductionsController {

    private final DeductionsService deductionsService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new deduction", description = "Creates a new deduction with the provided details. Requires ADMIN role.")
    public ResponseEntity<DeductionsResponseDto> createDeduction(
            @Valid @RequestBody DeductionsRequestDto deductionsRequestDto,
            UriComponentsBuilder uriBuilder) {
        DeductionsResponseDto createdDeduction = deductionsService.createDeduction(deductionsRequestDto);
        var uri = uriBuilder.path("/api/v1/deductions/{id}").buildAndExpand(createdDeduction.id()).toUri();
        return ResponseEntity.created(uri).body(createdDeduction);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Get deduction by ID", description = "Retrieves a deduction by its ID. Requires MANAGER or ADMIN role.")
    public ResponseEntity<DeductionsResponseDto> getDeductionById(@PathVariable UUID id) {
        return ResponseEntity.ok(deductionsService.getDeductionById(id));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Get deduction by name", description = "Retrieves a deduction by its name. Requires MANAGER or ADMIN role.")
    public ResponseEntity<DeductionsResponseDto> getDeductionByName(@PathVariable String name) {
        return ResponseEntity.ok(deductionsService.getDeductionByName(name));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get all deductions", description = "Retrieves a list of all deductions. Accessible to all authenticated users.")
    public ResponseEntity<List<DeductionsResponseDto>> getAllDeductions() {
        return ResponseEntity.ok(deductionsService.getAllDeductions());
    }

    @PatchMapping("/{id}/percentage")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update deduction percentage", description = "Updates the percentage of a deduction. Requires ADMIN role.")
    public ResponseEntity<DeductionsResponseDto> updateDeductionPercentage(
            @PathVariable UUID id,
            @RequestParam double percentage) {
        return ResponseEntity.ok(deductionsService.updateDeductionPercentage(id, percentage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete deduction", description = "Deletes a deduction by its ID. Requires ADMIN role.")
    public ResponseEntity<Void> deleteDeduction(@PathVariable UUID id) {
        deductionsService.deleteDeduction(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/initialize-defaults")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Initialize default deductions", description = "Initializes the default deductions if they don't exist. Requires ADMIN role.")
    public ResponseEntity<Void> initializeDefaultDeductions() {
        deductionsService.initializeDefaultDeductions();
        return ResponseEntity.ok().build();
    }
}