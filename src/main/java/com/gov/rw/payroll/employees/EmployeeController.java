package com.gov.rw.payroll.employees;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.gov.rw.payroll.auth.dtos.RegisterRequestDto;
import com.gov.rw.payroll.employees.dtos.EmployeeResponseDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@AllArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Create a new employee", description = "Creates a new employee with the provided details. Requires MANAGER role.")
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @Valid @RequestBody RegisterRequestDto employeeRequestDto,
            UriComponentsBuilder uriBuilder) {
        EmployeeResponseDto createdEmployee = employeeService.registerEmployee(employeeRequestDto);
        var uri = uriBuilder.path("/api/v1/employees/{id}").buildAndExpand(createdEmployee.id()).toUri();
        return ResponseEntity.created(uri).body(createdEmployee);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get employee by ID", description = "Retrieves an employee by their ID. Accessible to all authenticated users.")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Get employee by email", description = "Retrieves an employee by their email. Requires MANAGER or ADMIN role.")
    public ResponseEntity<EmployeeResponseDto> getEmployeeByEmail(@PathVariable String email) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmail(email));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees. Requires MANAGER or ADMIN role.")
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Update employee status", description = "Updates the status of an employee. Requires MANAGER or ADMIN role.")
    public ResponseEntity<EmployeeResponseDto> updateEmployeeStatus(
            @PathVariable UUID id,
            @RequestParam Status status) {
        return ResponseEntity.ok(employeeService.updateEmployeeStatus(id, status));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update employee role", description = "Updates the role of an employee. Requires ADMIN role.")
    public ResponseEntity<EmployeeResponseDto> updateEmployeeRole(
            @PathVariable UUID id,
            @RequestParam Role role) {
        return ResponseEntity.ok(employeeService.updateEmployeeRole(id, role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete employee", description = "Deletes an employee by their ID. Requires ADMIN role.")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}