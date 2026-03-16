package com.example.nuocuong.service;

public interface OtpService {
	/**
	 * Sinh OTP và gửi (email giả lập) cho email.
	 */
	void guiOtpDangKy(String email);

	/**
	 * Xác thực OTP. Trả về true nếu hợp lệ.
	 */
	boolean xacThucOtp(String email, String otp);
}

