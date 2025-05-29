package com.gov.rw.payroll.paySlip;

import jakarta.persistence.*;
import lombok.*;
import com.gov.rw.payroll.employees.Employee;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "pay_slip")

@Entity
public class PaySlip {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String position;

    @OneToOne(fetch = FetchType.EAGER)
    private Employee employee;

    @Column(name = "house_amount", nullable = false)
    private double houseAmount;

    @Column(name = "transport_amount", nullable = false)
    private double transportAmount;

    @Column(name = "employee_taxed_amount", nullable = false)
    private double employeeTaxedAmount;

    @Column(name = "pension_amount", nullable = false)
    private double pensionAmount;

    @Column(name = "medical_insurance_amount", nullable = false)
    private double medicalInsuranceAmount;

    @Column(name = "other_taxed_amount", nullable = false)
    private double otherTaxedAmount;

    @Column(name = "gross_salary", nullable = false)
    private double grossSalary;

    @Column(name = "net_salary", nullable = false)
    private double netSalary;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "year", nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;



    public Status getStatus() {
        return status;
    }

}
