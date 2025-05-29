package com.gov.rw.payroll.employees;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_email_unq", columnList = "email", unique = true),
        @Index(name = "idx_employee_mobile_unq", columnList = "mobile", unique = true),
})

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 10)
    private String mobile;

    private boolean enabled = false;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DISABLED;

    @Column(name = "date_of_birth")
    private LocalDate dob;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roles= Role.ROLE_EMPLOYEE;

    public Status getStatus() {
        return status;
    }

}
