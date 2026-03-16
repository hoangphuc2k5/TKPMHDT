package com.example.nuocuong.factory;

import com.example.nuocuong.dto.TuyChinhDto;
import com.example.nuocuong.entity.TuyChinhKhachHang;
import org.springframework.stereotype.Component;

/**
 * Factory Pattern:
 * - Đóng gói việc tạo đối tượng tùy chỉnh đồ uống từ dữ liệu UI/DTO.
 * - Giúp service không phải "new" và rải logic khởi tạo ở nhiều nơi (SRP).
 */
@Component
public class CustomDrinkFactory {
	public TuyChinhKhachHang taoTuyChinh(TuyChinhDto dto) {
		if (dto == null) return null;

		return TuyChinhKhachHang.builder()
			.mucDuong(dto.getMucDuong())
			.mucDa(dto.getMucDa())
			.topping(dto.getTopping())
			.ghiChu(dto.getGhiChu())
			.build();
	}
}

