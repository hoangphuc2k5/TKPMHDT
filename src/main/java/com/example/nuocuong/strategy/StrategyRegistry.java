package com.example.nuocuong.strategy;

import com.example.nuocuong.entity.LoaiMaGiamGia;
import com.example.nuocuong.entity.PhuongThucThanhToan;
import com.example.nuocuong.exception.BusinessException;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * DIP: Service chỉ phụ thuộc vào registry (interface/abstraction) để lấy strategy.
 * Strategy Pattern: gom lookup strategy theo enum.
 */
@Component
public class StrategyRegistry {
	private final Map<PhuongThucThanhToan, PaymentStrategy> paymentStrategies = new EnumMap<>(PhuongThucThanhToan.class);
	private final Map<LoaiMaGiamGia, DiscountStrategy> discountStrategies = new EnumMap<>(LoaiMaGiamGia.class);

	public StrategyRegistry(
		CodPaymentStrategy codPaymentStrategy,
		OnlineMockPaymentStrategy onlineMockPaymentStrategy,
		PercentDiscountStrategy percentDiscountStrategy,
		FixedAmountDiscountStrategy fixedAmountDiscountStrategy
	) {
		paymentStrategies.put(PhuongThucThanhToan.COD, codPaymentStrategy);
		paymentStrategies.put(PhuongThucThanhToan.ONLINE_MOCK, onlineMockPaymentStrategy);

		discountStrategies.put(LoaiMaGiamGia.PHAN_TRAM, percentDiscountStrategy);
		discountStrategies.put(LoaiMaGiamGia.SO_TIEN_CO_DINH, fixedAmountDiscountStrategy);
	}

	public PaymentStrategy payment(PhuongThucThanhToan method) {
		PaymentStrategy s = paymentStrategies.get(method);
		if (s == null) throw new BusinessException("Không hỗ trợ phương thức thanh toán: " + method);
		return s;
	}

	public DiscountStrategy discount(LoaiMaGiamGia type) {
		DiscountStrategy s = discountStrategies.get(type);
		if (s == null) throw new BusinessException("Không hỗ trợ loại mã giảm giá: " + type);
		return s;
	}
}

