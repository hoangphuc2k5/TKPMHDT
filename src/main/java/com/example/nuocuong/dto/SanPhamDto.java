package com.example.nuocuong.dto;

import com.example.nuocuong.entity.LoaiSanPham;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamDto {
	Long id;
	String ten;
	String moTa;
	BigDecimal giaBan;
	LoaiSanPham loai;
	String hinhAnhUrl;
	boolean dangKinhDoanh;
}

