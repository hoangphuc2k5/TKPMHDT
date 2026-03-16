package com.example.nuocuong.dto;

import com.example.nuocuong.entity.TrangThaiDonHang;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {
	Long id;
	String maDonHang;
	Long khachHangId;
	TrangThaiDonHang trangThai;
	String diaChiGiaoHang;
	BigDecimal tongTienHang;
	BigDecimal giamGia;
	BigDecimal tongThanhToan;
	LocalDateTime createdAt;
	PaymentDto thanhToan;
	String maGiamGia;
	List<OrderItemDto> items;
}

