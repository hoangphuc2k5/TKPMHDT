package com.example.nuocuong.strategy;

import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.PhuongThucThanhToan;
import com.example.nuocuong.entity.ThanhToan;
import com.example.nuocuong.entity.TrangThaiThanhToan;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OnlineMockPaymentStrategy implements PaymentStrategy {
	@Override
	public ThanhToan thucHienThanhToan(DonHang donHang, BigDecimal soTien) {
		// Online giả lập: coi như thanh toán thành công ngay.
		return ThanhToan.builder()
			.donHang(donHang)
			.phuongThuc(PhuongThucThanhToan.ONLINE_MOCK)
			.trangThai(TrangThaiThanhToan.DA_THANH_TOAN)
			.soTien(soTien)
			.maGiaoDich("MOCK-" + UUID.randomUUID())
			.thanhToanLuc(LocalDateTime.now())
			.build();
	}
}

