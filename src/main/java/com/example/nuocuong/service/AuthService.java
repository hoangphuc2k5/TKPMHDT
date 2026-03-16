package com.example.nuocuong.service;

import com.example.nuocuong.dto.AuthResponse;
import com.example.nuocuong.dto.LoginRequest;
import com.example.nuocuong.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
