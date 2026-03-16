package com.example.nuocuong.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderItemDto {
	Long id;
	Long sanPhamId;
	String tenSanPham;
	int soLuong;
	BigDecimal donGia;
	BigDecimal thanhTien;
	TuyChinhDto tuyChinh;
}

