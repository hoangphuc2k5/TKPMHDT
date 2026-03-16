package com.example.nuocuong.strategy;

import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.PhuongThucThanhToan;
import com.example.nuocuong.entity.ThanhToan;
import com.example.nuocuong.entity.TrangThaiThanhToan;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CodPaymentStrategy implements PaymentStrategy {
	@Override
	public ThanhToan thucHienThanhToan(DonHang donHang, BigDecimal soTien) {
		// COD: trạng thái chờ thanh toán đến khi giao hàng thu tiền.
		return ThanhToan.builder()
			.donHang(donHang)
			.phuongThuc(PhuongThucThanhToan.COD)
			.trangThai(TrangThaiThanhToan.CHO_THANH_TOAN)
			.soTien(soTien)
			.build();
	}
}

