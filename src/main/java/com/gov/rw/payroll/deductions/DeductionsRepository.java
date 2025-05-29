package com.gov.rw.payroll.deductions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeductionsRepository extends JpaRepository<Deductions, UUID> {
    Optional<Deductions> findByCode(String code);
    Optional<Deductions> findByDeductionName(String deductionName);
}