package com.gov.rw.payroll.employees;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gov.rw.payroll.auth.dtos.RegisterRequestDto;
import com.gov.rw.payroll.commons.exceptions.BadRequestException;
import com.gov.rw.payroll.commons.exceptions.ResourceNotFoundException;
import com.gov.rw.payroll.employees.dtos.EmployeeResponseDto;
import com.gov.rw.payroll.employees.mappers.EmployeeMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:verygoodmuhirwa2@gmail.com}")
    private String adminEmail;

    @Value("${admin.password:*verygoodmuhirwa12345#}")
    private String adminPassword;


    public void changeUserPassword(String userEmail, String newPassword) {
        var user = findByEmail(userEmail);
        user.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(user);
    }

    public void activateUserAccount(String userEmail) {
        var employee = findByEmail(userEmail);
        employee.setEnabled(true);
        employeeRepository.save(employee);
    }

    public void updateUserStatus(String email, Status newStatus) {
        var employee = findByEmail(email);
        employee.setStatus(newStatus);
        employeeRepository.save(employee);
    }

    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("User with that email not found."));
    }

    public Employee getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new BadRequestException("User is not authenticated.");
        }

        String userId = auth.getName(); // This is the user ID
        return employeeRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BadRequestException("User with that ID not found."));
    }

    public EmployeeResponseDto getCurrentLoggedInUser() {
        return employeeMapper.toResponseDto(getAuthenticatedUser());
    }

    public java.util.Optional<Employee> findById(String userId) {
        return employeeRepository.findById(UUID.fromString(userId));
    }

    public void createAdminUserIfNotExists() {
        if (!employeeRepository.findByEmail(adminEmail).isPresent()) {
            Employee adminUser = new Employee();
            adminUser.setId(UUID.randomUUID());
            adminUser.setCode(employeeMapper.generateEmployeeCode());
            adminUser.setFirstName("Verygood");
            adminUser.setLastName("Muhirwa");
            adminUser.setEnabled(true);
            adminUser.setStatus(Status.ACTIVE);
            adminUser.setMobile("0798978831");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRoles(Role.ROLE_ADMIN);
            adminUser.setDob(LocalDate.of(2005, 14, 14));
            employeeRepository.save(adminUser);
            log.info("Admin user 'Angele' created.");
        } else {
            log.info("Admin user 'Verygood' already exists.");
        }
    }



    public EmployeeResponseDto createEmployee(RegisterRequestDto dto) {
        if (employeeRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("Employee with this email already exists.");
        }
        if (employeeRepository.existsByMobile(dto.mobile())) {
            throw new BadRequestException("Employee with this mobile number already exists.");
        }

        Employee employee = employeeMapper.toEntity(dto);
        employee.setPassword(passwordEncoder.encode(dto.password()));
        employee.setRoles(Role.ROLE_MANAGER);
        employeeRepository.save(employee);
        log.info("Employee created: {}", employee.getEmail());
        return employeeMapper.toResponseDto(employee);
    }

    public EmployeeResponseDto registerEmployee(RegisterRequestDto dto) {
        if (employeeRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("Employee with this email already exists.");
        }
        if (employeeRepository.existsByMobile(dto.mobile())) {
            throw new BadRequestException("Employee with this mobile number already exists.");
        }

        Employee employee = employeeMapper.toEntity(dto);
        employee.setPassword(passwordEncoder.encode(dto.password()));
        employeeRepository.save(employee);
        log.info("Employee created: {}", employee.getEmail());
        return employeeMapper.toResponseDto(employee);
    }

    public EmployeeResponseDto getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return employeeMapper.toResponseDto(employee);
    }

    public EmployeeResponseDto getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "email", email));
        return employeeMapper.toResponseDto(employee);
    }

    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public EmployeeResponseDto updateEmployeeStatus(UUID id, Status status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setStatus(status);
        employeeRepository.save(employee);
        log.info("Employee status updated: {}", employee.getEmail());
        return employeeMapper.toResponseDto(employee);
    }

    public EmployeeResponseDto updateEmployeeRole(UUID id, Role role) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setRoles(role);
        employeeRepository.save(employee);
        log.info("Employee role updated: {}", employee.getEmail());
        return employeeMapper.toResponseDto(employee);
    }

    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeRepository.deleteById(id);
        log.info("Employee deleted with id: {}", id);
    }
}