package com.example.nuocuong.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthLoginRequest {
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	String email;

	@NotBlank(message = "Mật khẩu không được để trống")
	String matKhau;
}

