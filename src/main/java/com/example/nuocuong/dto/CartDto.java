package com.example.nuocuong.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CartDto {
	Long gioHangId;
	Long khachHangId;
	List<CartItemDto> items;
	BigDecimal tongTien;
}

