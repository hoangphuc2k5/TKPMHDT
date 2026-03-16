package com.example.nuocuong.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CartItemDto {
	Long id;
	Long sanPhamId;
	String tenSanPham;
	int soLuong;
	BigDecimal donGia;
	BigDecimal thanhTien;
}

