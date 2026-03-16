package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.AuthRegisterRequest;
import com.example.nuocuong.dto.NguoiDungDto;
import com.example.nuocuong.dto.OtpVerifyRequest;
import com.example.nuocuong.entity.KhachHang;
import com.example.nuocuong.entity.VaiTro;
import com.example.nuocuong.exception.BusinessException;
import com.example.nuocuong.exception.NotFoundException;
import com.example.nuocuong.repository.KhachHangRepository;
import com.example.nuocuong.service.AuthService;
import com.example.nuocuong.service.OtpService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Layer Pattern:
 * - Business logic chỉ nằm ở Service (không để trong Controller/Repository).
 * DTO Pattern:
 * - Public API của service dùng DTO/request DTO.
 */
@Service
public class AuthServiceImpl implements AuthService {
	private final KhachHangRepository khachHangRepository;
	private final OtpService otpService;
	private final PasswordEncoder passwordEncoder;

	public AuthServiceImpl(
		KhachHangRepository khachHangRepository,
		OtpService otpService,
		PasswordEncoder passwordEncoder
	) {
		this.khachHangRepository = khachHangRepository;
		this.otpService = otpService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public NguoiDungDto dangKyKhachHang(AuthRegisterRequest request) {
		if (khachHangRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email đã tồn tại");
		}

		KhachHang kh = KhachHang.builder()
			.email(request.getEmail().trim().toLowerCase())
			.matKhauMaHoa(passwordEncoder.encode(request.getMatKhau()))
			.vaiTro(VaiTro.KHACH_HANG)
			.kichHoat(false)
			.hoTen(request.getHoTen())
			.build();

		kh = khachHangRepository.save(kh);
		otpService.guiOtpDangKy(kh.getEmail());

		return NguoiDungDto.builder()
			.id(kh.getId())
			.email(kh.getEmail())
			.hoTen(kh.getHoTen())
			.vaiTro(kh.getVaiTro())
			.kichHoat(kh.isKichHoat())
			.build();
	}

	@Override
	@Transactional
	public void xacThucOtpVaKichHoat(OtpVerifyRequest request) {
		boolean ok = otpService.xacThucOtp(request.getEmail(), request.getOtp());
		if (!ok) throw new BusinessException("OTP không hợp lệ hoặc đã hết hạn");

		KhachHang kh = khachHangRepository.findByEmail(request.getEmail().trim().toLowerCase())
			.orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));

		kh.setKichHoat(true);
		khachHangRepository.save(kh);
	}
}

