package com.example.nuocuong.service.impl.builder;

import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.KhachHang;
import com.example.nuocuong.entity.MaGiamGia;
import com.example.nuocuong.entity.TrangThaiDonHang;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder Pattern:
 * - Dựng DonHang theo từng bước để dễ mở rộng, giảm coupling trong service.
 */
public class DonHangBuilder {
	private KhachHang khachHang;
	private String diaChiGiaoHang;
	private MaGiamGia maGiamGia;
	private TrangThaiDonHang trangThai = TrangThaiDonHang.CHO_XAC_NHAN;

	private BigDecimal tongTienHang = BigDecimal.ZERO;
	private BigDecimal giamGia = BigDecimal.ZERO;
	private BigDecimal tongThanhToan = BigDecimal.ZERO;

	public static DonHangBuilder builder() {
		return new DonHangBuilder();
	}

	public DonHangBuilder khachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
		return this;
	}

	public DonHangBuilder diaChiGiaoHang(String diaChiGiaoHang) {
		this.diaChiGiaoHang = diaChiGiaoHang;
		return this;
	}

	public DonHangBuilder maGiamGia(MaGiamGia maGiamGia) {
		this.maGiamGia = maGiamGia;
		return this;
	}

	public DonHangBuilder trangThai(TrangThaiDonHang trangThai) {
		this.trangThai = trangThai;
		return this;
	}

	public DonHangBuilder tongTienHang(BigDecimal tongTienHang) {
		this.tongTienHang = tongTienHang;
		return this;
	}

	public DonHangBuilder giamGia(BigDecimal giamGia) {
		this.giamGia = giamGia;
		return this;
	}

	public DonHangBuilder tongThanhToan(BigDecimal tongThanhToan) {
		this.tongThanhToan = tongThanhToan;
		return this;
	}

	public DonHang build() {
		return DonHang.builder()
			.maDonHang("DH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
			.khachHang(khachHang)
			.diaChiGiaoHang(diaChiGiaoHang)
			.maGiamGia(maGiamGia)
			.trangThai(trangThai)
			.tongTienHang(tongTienHang)
			.giamGia(giamGia)
			.tongThanhToan(tongThanhToan)
			.createdAt(LocalDateTime.now())
			.build();
	}
}

