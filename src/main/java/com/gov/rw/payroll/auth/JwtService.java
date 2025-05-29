package com.gov.rw.payroll.auth;

import com.gov.rw.payroll.auth.exceptions.InvalidJwtException;
import com.gov.rw.payroll.employees.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
@Slf4j
public class JwtService {
    private final JwtConfig config;

    public String generateAccessToken(Employee employee){
        return generateToken(employee, config.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Employee employee){
        return generateToken(employee, config.getRefreshTokenExpiration());
    }

    private String generateToken(Employee Employee, long tokenExpiration){
        return Jwts.builder()
                .subject(Employee.getId().toString())
                .claim("email", Employee.getEmail())
                .claim("phoneNumber", Employee.getMobile())
                .claim("role", Employee.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .signWith(config.getSecretKey())
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return getClaims(token);
        } catch (ExpiredJwtException ex) {
            log.debug("Token expired: {}", ex.getMessage());
            throw new InvalidJwtException("Token expired");
        } catch (SignatureException ex) {
            log.debug("Invalid token signature: {}", ex.getMessage());
            throw new InvalidJwtException("Invalid token signature");
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid token: {}", ex.getMessage());
            throw new InvalidJwtException("Invalid token");
        }
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(config.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}