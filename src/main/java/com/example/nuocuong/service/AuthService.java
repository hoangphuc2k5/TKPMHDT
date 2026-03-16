package com.example.nuocuong.service;

import com.example.nuocuong.dto.AuthRegisterRequest;
import com.example.nuocuong.dto.NguoiDungDto;
import com.example.nuocuong.dto.OtpVerifyRequest;

public interface AuthService {
	/**
	 * Đăng ký (tạo user trạng thái chưa kích hoạt) + gửi OTP (email giả lập).
	 */
	NguoiDungDto dangKyKhachHang(AuthRegisterRequest request);

	/**
	 * Xác thực OTP để kích hoạt tài khoản.
	 */
	void xacThucOtpVaKichHoat(OtpVerifyRequest request);
}

