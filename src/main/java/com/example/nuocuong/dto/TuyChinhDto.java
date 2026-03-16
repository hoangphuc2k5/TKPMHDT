package com.example.nuocuong.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TuyChinhDto {
	@Min(0)
	@Max(200)
	int mucDuong;

	@Min(0)
	@Max(200)
	int mucDa;

	String topping;
	String ghiChu;
}

