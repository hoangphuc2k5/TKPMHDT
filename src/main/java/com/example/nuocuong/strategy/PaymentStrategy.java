package com.example.nuocuong.strategy;

import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.ThanhToan;
import java.math.BigDecimal;

public interface PaymentStrategy {
	/**
	 * Strategy Pattern: mỗi loại thanh toán là một strategy riêng.
	 */
	ThanhToan thucHienThanhToan(DonHang donHang, BigDecimal soTien);
}

