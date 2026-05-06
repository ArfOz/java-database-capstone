package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    // =========================
    // SIGNING KEY
    // =========================
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // =========================
    // GENERATE TOKEN
    // =========================
    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // EXTRACT EMAIL
    // =========================
    public String extractEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // =========================
    // VALIDATE TOKEN
    // =========================
    public boolean validateToken(String token, String role) {

        try {
            String email = extractEmail(token);

            if (role.equals("admin")) {
                return adminRepository.findByEmail(email).isPresent();
            }

            if (role.equals("doctor")) {
                return doctorRepository.findByEmail(email).isPresent();
            }

            if (role.equals("patient")) {
                return patientRepository.findByEmail(email).isPresent();
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }
}