package com.example.nuocuong.service.impl.builder;

import com.example.nuocuong.entity.ChiTietDonHang;
import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.SanPham;
import com.example.nuocuong.entity.TuyChinhKhachHang;
import java.math.BigDecimal;

/**
 * Builder Pattern:
 * - Dựng ChiTietDonHang theo từng bước (khi dữ liệu input phức tạp).
 */
public class ChiTietDonHangBuilder {
	private DonHang donHang;
	private SanPham sanPham;
	private int soLuong;
	private BigDecimal donGia;
	private TuyChinhKhachHang tuyChinhKhachHang;

	public static ChiTietDonHangBuilder builder() {
		return new ChiTietDonHangBuilder();
	}

	public ChiTietDonHangBuilder donHang(DonHang donHang) {
		this.donHang = donHang;
		return this;
	}

	public ChiTietDonHangBuilder sanPham(SanPham sanPham) {
		this.sanPham = sanPham;
		return this;
	}

	public ChiTietDonHangBuilder soLuong(int soLuong) {
		this.soLuong = soLuong;
		return this;
	}

	public ChiTietDonHangBuilder donGia(BigDecimal donGia) {
		this.donGia = donGia;
		return this;
	}

	public ChiTietDonHangBuilder tuyChinh(TuyChinhKhachHang tuyChinhKhachHang) {
		this.tuyChinhKhachHang = tuyChinhKhachHang;
		return this;
	}

	public ChiTietDonHang build() {
		BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
		return ChiTietDonHang.builder()
			.donHang(donHang)
			.sanPham(sanPham)
			.soLuong(soLuong)
			.donGia(donGia)
			.thanhTien(thanhTien)
			.tuyChinhKhachHang(tuyChinhKhachHang)
			.build();
	}
}

