package com.example.nuocuong.strategy;

import com.example.nuocuong.entity.MaGiamGia;
import java.math.BigDecimal;

public interface DiscountStrategy {
	/**
	 * Strategy Pattern: mỗi loại giảm giá có cách tính khác nhau.
	 */
	BigDecimal tinhGiamGia(BigDecimal tongTienHang, MaGiamGia maGiamGia);
}

