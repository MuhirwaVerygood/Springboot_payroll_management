package com.gov.rw.payroll.employment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.gov.rw.payroll.employment.dtos.EmploymentRequestDto;
import com.gov.rw.payroll.employment.dtos.EmploymentResponseDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employments")
@AllArgsConstructor
@Tag(name = "Employment Management", description = "APIs for managing employments")
public class EmploymentController {

    private final EmploymentService employmentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Create a new employment", description = "Creates a new employment record with the provided details. Requires MANAGER role.")
    public ResponseEntity<EmploymentResponseDto> createEmployment(
            @Valid @RequestBody EmploymentRequestDto employmentRequestDto,
            UriComponentsBuilder uriBuilder) {
        EmploymentResponseDto createdEmployment = employmentService.createEmployment(employmentRequestDto);
        var uri = uriBuilder.path("/api/v1/employments/{id}").buildAndExpand(createdEmployment.id()).toUri();
        return ResponseEntity.created(uri).body(createdEmployment);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get employment by ID", description = "Retrieves an employment record by its ID. Accessible to all authenticated users.")
    public ResponseEntity<EmploymentResponseDto> getEmploymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(employmentService.getEmploymentById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get employments by employee ID", description = "Retrieves all employment records for a specific employee. Accessible to all authenticated users.")
    public ResponseEntity<List<EmploymentResponseDto>> getEmploymentsByEmployeeId(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployeeId(employeeId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all employments", description = "Retrieves a list of all employment records. Requires MANAGER or ADMIN role.")
    public ResponseEntity<List<EmploymentResponseDto>> getAllEmployments() {
        return ResponseEntity.ok(employmentService.getAllEmployments());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update employment status", description = "Updates the status of an employment record. Requires MANAGER role.")
    public ResponseEntity<EmploymentResponseDto> updateEmploymentStatus(
            @PathVariable UUID id,
            @RequestParam Status status) {
        return ResponseEntity.ok(employmentService.updateEmploymentStatus(id, status));
    }

    @PatchMapping("/{id}/salary")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update employment base salary", description = "Updates the base salary of an employment record. Requires MANAGER role.")
    public ResponseEntity<EmploymentResponseDto> updateEmploymentBaseSalary(
            @PathVariable UUID id,
            @RequestParam double baseSalary) {
        return ResponseEntity.ok(employmentService.updateEmploymentBaseSalary(id, baseSalary));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete employment", description = "Deletes an employment record by its ID. Requires ADMIN role.")
    public ResponseEntity<Void> deleteEmployment(@PathVariable UUID id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.noContent().build();
    }
}