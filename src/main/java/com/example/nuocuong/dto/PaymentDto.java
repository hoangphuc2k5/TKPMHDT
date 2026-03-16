package com.example.nuocuong.dto;

import com.example.nuocuong.entity.PhuongThucThanhToan;
import com.example.nuocuong.entity.TrangThaiThanhToan;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentDto {
	Long id;
	PhuongThucThanhToan phuongThuc;
	TrangThaiThanhToan trangThai;
	BigDecimal soTien;
	String maGiaoDich;
	LocalDateTime thanhToanLuc;
}

