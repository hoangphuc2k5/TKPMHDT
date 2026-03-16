package com.example.nuocuong.dto;

import com.example.nuocuong.entity.LoaiMaGiamGia;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MaGiamGiaDto {
	Long id;
	String ma;
	String moTa;
	LoaiMaGiamGia loai;
	BigDecimal giaTri;
	BigDecimal donToiThieu;
	boolean kichHoat;
	LocalDateTime batDauLuc;
	LocalDateTime ketThucLuc;
}

