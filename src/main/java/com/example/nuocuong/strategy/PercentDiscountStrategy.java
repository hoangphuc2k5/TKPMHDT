package com.example.nuocuong.strategy;

import com.example.nuocuong.entity.MaGiamGia;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PercentDiscountStrategy implements DiscountStrategy {
	@Override
	public BigDecimal tinhGiamGia(BigDecimal tongTienHang, MaGiamGia maGiamGia) {
		if (tongTienHang == null || maGiamGia == null) return BigDecimal.ZERO;
		BigDecimal pct = maGiamGia.getGiaTri();
		if (pct == null) return BigDecimal.ZERO;
		// giaTri = % (0-100)
		BigDecimal discount = tongTienHang.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		return discount.max(BigDecimal.ZERO);
	}
}

