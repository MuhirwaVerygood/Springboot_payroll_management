package com.gov.rw.payroll.paySlip;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.gov.rw.payroll.paySlip.dtos.PaySlipRequestDto;
import com.gov.rw.payroll.paySlip.dtos.PaySlipResponseDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payslips")
@AllArgsConstructor
@Tag(name = "Payroll Management", description = "APIs for managing payroll and pay slips")
public class PaySlipController {

    private final PaySlipService paySlipService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Generate a pay slip", description = "Generates a pay slip for an employee for a specific month and year. Requires MANAGER role.")
    public ResponseEntity<PaySlipResponseDto> generatePaySlip(
            @Valid @RequestBody PaySlipRequestDto paySlipRequestDto,
            UriComponentsBuilder uriBuilder) {
        PaySlipResponseDto generatedPaySlip = paySlipService.generatePaySlip(paySlipRequestDto);
        var uri = uriBuilder.path("/api/v1/payslips/{id}").buildAndExpand(generatedPaySlip.id()).toUri();
        return ResponseEntity.created(uri).body(generatedPaySlip);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get pay slip by ID", description = "Retrieves a pay slip by its ID. Accessible to all authenticated users.")
    public ResponseEntity<PaySlipResponseDto> getPaySlipById(@PathVariable UUID id) {
        return ResponseEntity.ok(paySlipService.getPaySlipById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get pay slips by employee ID", description = "Retrieves all pay slips for a specific employee. Accessible to all authenticated users.")
    public ResponseEntity<List<PaySlipResponseDto>> getPaySlipsByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(paySlipService.getPaySlipsByEmployee(employeeId));
    }

    @GetMapping("/month-year")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Get pay slips by month and year", description = "Retrieves all pay slips for a specific month and year. Requires MANAGER or ADMIN role.")
    public ResponseEntity<List<PaySlipResponseDto>> getPaySlipsByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(paySlipService.getPaySlipsByMonthAndYear(month, year));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all pay slips", description = "Retrieves a list of all pay slips. Requires MANAGER or ADMIN role.")
    public ResponseEntity<List<PaySlipResponseDto>> getAllPaySlips() {
        return ResponseEntity.ok(paySlipService.getAllPaySlips());
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Approve pay slip", description = "Approves a pay slip, changing its status from PENDING to PAID. Requires ADMIN role.")
    public ResponseEntity<PaySlipResponseDto> approvePaySlip(@PathVariable UUID id) {
        return ResponseEntity.ok(paySlipService.approvePaySlip(id));
    }

    @PatchMapping("/approve-all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Approve all pending pay slips", description = "Approves all pending pay slips, changing their status from PENDING to PAID. Requires ADMIN role.")
    public ResponseEntity<List<PaySlipResponseDto>> approveAllPendingPaySlips() {
        return ResponseEntity.ok(paySlipService.approveAllPendingPaySlips());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete pay slip", description = "Deletes a pay slip by its ID. Requires ADMIN role.")
    public ResponseEntity<Void> deletePaySlip(@PathVariable UUID id) {
        paySlipService.deletePaySlip(id);
        return ResponseEntity.noContent().build();
    }
}