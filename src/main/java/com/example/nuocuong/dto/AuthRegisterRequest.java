package com.example.nuocuong.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthRegisterRequest {
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	String email;

	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 6, max = 50, message = "Mật khẩu từ 6-50 ký tự")
	String matKhau;

	@NotBlank(message = "Họ tên không được để trống")
	@Size(max = 120, message = "Họ tên tối đa 120 ký tự")
	String hoTen;
}

