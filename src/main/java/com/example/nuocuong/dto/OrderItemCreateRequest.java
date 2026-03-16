package com.example.nuocuong.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderItemCreateRequest {
	@NotNull(message = "Sản phẩm không được để trống")
	Long sanPhamId;

	@Min(value = 1, message = "Số lượng tối thiểu là 1")
	int soLuong;

	@Valid
	TuyChinhDto tuyChinh; // optional
}

