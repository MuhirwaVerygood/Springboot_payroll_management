package com.gov.rw.payroll.commons.response;

import lombok.Getter;
import lombok.Setter;
import com.gov.rw.payroll.employees.Employee;

@Getter
@Setter
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Employee employee;

    public JwtAuthenticationResponse(String accessToken, Employee employee) {
        this.accessToken = accessToken;
        this.employee = employee;
    }
}




