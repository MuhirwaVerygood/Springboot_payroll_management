package com.gov.rw.payroll.employment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, UUID> {
    List<Employment> findByEmployeeId(UUID employeeId);
    Optional<Employment> findByCode(String code);
    List<Employment> findByStatus(Status status);
}