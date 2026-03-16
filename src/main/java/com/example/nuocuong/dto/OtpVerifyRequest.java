package com.example.nuocuong.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OtpVerifyRequest {
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	String email;

	@NotBlank(message = "OTP không được để trống")
	@Pattern(regexp = "^[0-9]{6}$", message = "OTP gồm 6 chữ số")
	String otp;
}

