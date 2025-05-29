package com.gov.rw.payroll.paySlip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gov.rw.payroll.employees.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaySlipRepository extends JpaRepository<PaySlip, UUID> {
    List<PaySlip> findByEmployee(Employee employee);
    List<PaySlip> findByEmployeeAndStatus(Employee employee, Status status);
    List<PaySlip> findByStatus(Status status);
    List<PaySlip> findByMonthAndYear(int month, int year);
    Optional<PaySlip> findByEmployeeAndMonthAndYear(Employee employee, int month, int year);
}