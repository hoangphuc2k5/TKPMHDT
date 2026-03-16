package com.example.nuocuong.service.impl;

import com.example.nuocuong.entity.MaGiamGia;
import com.example.nuocuong.exception.BusinessException;
import com.example.nuocuong.repository.MaGiamGiaRepository;
import com.example.nuocuong.service.MaGiamGiaService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaGiamGiaServiceImpl implements MaGiamGiaService {
	private final MaGiamGiaRepository maGiamGiaRepository;

	public MaGiamGiaServiceImpl(MaGiamGiaRepository maGiamGiaRepository) {
		this.maGiamGiaRepository = maGiamGiaRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public MaGiamGia timMaHopLe(String ma) {
		if (ma == null || ma.isBlank()) return null;
		MaGiamGia mg = maGiamGiaRepository.findByMa(ma.trim().toUpperCase()).orElse(null);
		if (mg == null) return null;
		if (!mg.isKichHoat()) return null;

		LocalDateTime now = LocalDateTime.now();
		if (mg.getBatDauLuc() != null && now.isBefore(mg.getBatDauLuc())) return null;
		if (mg.getKetThucLuc() != null && now.isAfter(mg.getKetThucLuc())) return null;
		return mg;
	}

	@Override
	public void validateDieuKien(BigDecimal tongTienHang, MaGiamGia maGiamGia) {
		if (maGiamGia == null) return;
		BigDecimal min = maGiamGia.getDonToiThieu() == null ? BigDecimal.ZERO : maGiamGia.getDonToiThieu();
		if (tongTienHang.compareTo(min) < 0) {
			throw new BusinessException("Đơn tối thiểu " + min + " để dùng mã " + maGiamGia.getMa());
		}
	}
}

