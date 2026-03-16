package com.example.nuocuong.dto;

import com.example.nuocuong.entity.PhuongThucThanhToan;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderCreateRequest {
	@NotNull(message = "Khách hàng không được để trống")
	Long khachHangId;

	@NotBlank(message = "Địa chỉ giao hàng không được để trống")
	String diaChiGiaoHang;

	String maGiamGia;

	@NotNull(message = "Phương thức thanh toán không được để trống")
	PhuongThucThanhToan phuongThucThanhToan;

	@Valid
	@NotNull(message = "Danh sách item không được để trống")
	List<OrderItemCreateRequest> items;
}

