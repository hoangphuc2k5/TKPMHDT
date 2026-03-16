package com.example.nuocuong.strategy;

import com.example.nuocuong.entity.MaGiamGia;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class FixedAmountDiscountStrategy implements DiscountStrategy {
	@Override
	public BigDecimal tinhGiamGia(BigDecimal tongTienHang, MaGiamGia maGiamGia) {
		if (tongTienHang == null || maGiamGia == null) return BigDecimal.ZERO;
		BigDecimal amount = maGiamGia.getGiaTri();
		if (amount == null) return BigDecimal.ZERO;
		return amount.max(BigDecimal.ZERO).min(tongTienHang);
	}
}

