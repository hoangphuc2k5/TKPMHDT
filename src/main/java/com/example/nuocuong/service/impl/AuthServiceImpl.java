package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.AuthResponse;
import com.example.nuocuong.dto.LoginRequest;
import com.example.nuocuong.dto.RegisterRequest;
import com.example.nuocuong.entity.KhachHang;
import com.example.nuocuong.entity.Role;
import com.example.nuocuong.repository.KhachHangRepository;
import com.example.nuocuong.security.JwtUtil;
import com.example.nuocuong.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getTenDangNhap(),
                        request.getMatKhau()
                )
        );
        var user = userDetailsService.loadUserByUsername(request.getTenDangNhap());
        var token = jwtUtil.generateToken(user);
        
        var nguoiDung = khachHangRepository.findByTenDangNhap(request.getTenDangNhap())
                .orElseThrow(); // Should be found if authentication succeeded

        return AuthResponse.builder()
                .token(token)
                .tenDangNhap(nguoiDung.getTenDangNhap())
                .vaiTro(nguoiDung.getVaiTro().name())
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        var khachHang = KhachHang.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .diaChi(request.getDiaChi())
                .vaiTro(Role.KHACH_HANG)
                .diemTichLuy(0)
                .build();
        
        khachHangRepository.save(khachHang);
        
        var userDetails = new User(
                khachHang.getTenDangNhap(),
                khachHang.getMatKhau(),
                Collections.emptyList()
        );
        var token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tenDangNhap(khachHang.getTenDangNhap())
                .vaiTro(Role.KHACH_HANG.name())
                .build();
    }
}
