package com.gov.rw.payroll.auth;

import com.gov.rw.payroll.auth.dtos.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.gov.rw.payroll.commons.exceptions.BadRequestException;
import com.gov.rw.payroll.email.EmailService;
import com.gov.rw.payroll.employees.EmployeeService;
import com.gov.rw.payroll.employees.dtos.EmployeeResponseDto;
import com.gov.rw.payroll.employees.Status;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.gov.rw.payroll.employees.mappers.EmployeeMapper;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final EmployeeMapper employeeMapper;
    private final OtpService otpService;
    private final EmailService emailService;



    @PostMapping("/register")
    @RateLimiter(name = "auth-rate-limiter")
    public ResponseEntity<EmployeeResponseDto> registerEmployee(@Valid @RequestBody
                                                                RegisterRequestDto user, UriComponentsBuilder uriBuilder){
        var userResponse = employeeService.createEmployee(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userResponse.id()).toUri();
        // Use otp service to send otp to a registered user
        var otpToSend = otpService.generateOtp(userResponse.email(), OtpType.VERIFY_ACCOUNT);

        // Send email to the user with the OTP
        emailService.sendAccountVerificationEmail(userResponse.email(), userResponse.firstName(), otpToSend);
        return ResponseEntity.created(uri).body(userResponse);
    }


    @PatchMapping("/verify-account")
    @RateLimiter(name = "otp-rate-limiter")
    ResponseEntity<?> verifyAccount(@Valid @RequestBody VerifyAccountDto verifyAccountRequest){
        if (!otpService.verifyOtp(verifyAccountRequest.email(), verifyAccountRequest.otp(), OtpType.VERIFY_ACCOUNT))
            throw new BadRequestException("Invalid email or OTP");

        employeeService.activateUserAccount(verifyAccountRequest.email());

        var employee = employeeService.findByEmail(verifyAccountRequest.email());
        employeeService.updateUserStatus(employee.getEmail(), Status.ACTIVE);

        emailService.sendVerificationSuccessEmail(employee.getEmail(), employee.getFirstName());

        return ResponseEntity.ok("Account Activated successfully");
    }


    @PostMapping("/initiate-password-reset")
    ResponseEntity<?> initiatePasswordReset(@Valid @RequestBody InitiatePasswordResetDto initiateRequest){
        var otpToSend = otpService.generateOtp(initiateRequest.email(), OtpType.FORGOT_PASSWORD);
        var employee = employeeService.findByEmail(initiateRequest.email());
        employeeService.updateUserStatus(employee.getEmail(), Status.DISABLED);
        emailService.sendResetPasswordOtp(employee.getEmail(), employee.getFirstName(), otpToSend);
        return ResponseEntity.ok("If your email is registered, you will receive an email with instructions to reset your password.");
    }



    @PatchMapping("/reset-password")
    @RateLimiter(name = "auth-rate-limiter")
    ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordRequest){
        if (!otpService.verifyOtp(resetPasswordRequest.email(), resetPasswordRequest.otp(), OtpType.FORGOT_PASSWORD)) {
            throw new BadRequestException("Invalid email or OTP");
        }

        // Change password
        employeeService.changeUserPassword(resetPasswordRequest.email(), resetPasswordRequest.newPassword());

        // Fetch user
        var employee = employeeService.findByEmail(resetPasswordRequest.email());

        employeeService.updateEmployeeStatus(employee.getId(), Status.ACTIVE);


        // Send success email
        emailService.sendResetPasswordSuccessEmail(employee.getEmail(), employee.getFirstName());

        return ResponseEntity.ok("Password reset went successfully. You can login with your new password.");
    }


    @PostMapping("/login")
    @RateLimiter(name = "auth-rate-limiter")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        var loginResult = authService.login(loginRequestDto, response);
        return ResponseEntity.ok(new LoginResponse(loginResult.accessToken()));
    }

    @PostMapping("/refresh")
    @RateLimiter(name = "auth-rate-limiter")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            throw new BadRequestException("No refresh token provided");
        }

        var claims = jwtService.parseToken(refreshToken);
        var employeeId = claims.getSubject();
        var employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new BadRequestException("Employee not found"));

        if (!employee.getStatus().equals(Status.ACTIVE)) {
            throw new IllegalStateException("Employee account is not active");
        }

        var newAccessToken = jwtService.generateAccessToken(employee);
        var newRefreshToken = jwtService.generateRefreshToken(employee);

        var cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days to match AuthService comment
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponse(newAccessToken));
    }
}
