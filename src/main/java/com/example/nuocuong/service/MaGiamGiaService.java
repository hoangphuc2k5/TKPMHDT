package com.example.nuocuong.service;

import com.example.nuocuong.entity.MaGiamGia;
import java.math.BigDecimal;

public interface MaGiamGiaService {
	MaGiamGia timMaHopLe(String ma);

	/**
	 * Kiểm tra điều kiện đơn tối thiểu.
	 */
	void validateDieuKien(BigDecimal tongTienHang, MaGiamGia maGiamGia);
}

