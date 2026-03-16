package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.CartAddItemRequest;
import com.example.nuocuong.dto.CartDto;
import com.example.nuocuong.dto.CartItemDto;
import com.example.nuocuong.entity.ChiTietGioHang;
import com.example.nuocuong.entity.GioHang;
import com.example.nuocuong.entity.KhachHang;
import com.example.nuocuong.entity.SanPham;
import com.example.nuocuong.exception.NotFoundException;
import com.example.nuocuong.repository.ChiTietGioHangRepository;
import com.example.nuocuong.repository.GioHangRepository;
import com.example.nuocuong.repository.KhachHangRepository;
import com.example.nuocuong.repository.SanPhamRepository;
import com.example.nuocuong.service.GioHangService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GioHangServiceImpl implements GioHangService {
	private final GioHangRepository gioHangRepository;
	private final ChiTietGioHangRepository chiTietGioHangRepository;
	private final KhachHangRepository khachHangRepository;
	private final SanPhamRepository sanPhamRepository;

	public GioHangServiceImpl(
		GioHangRepository gioHangRepository,
		ChiTietGioHangRepository chiTietGioHangRepository,
		KhachHangRepository khachHangRepository,
		SanPhamRepository sanPhamRepository
	) {
		this.gioHangRepository = gioHangRepository;
		this.chiTietGioHangRepository = chiTietGioHangRepository;
		this.khachHangRepository = khachHangRepository;
		this.sanPhamRepository = sanPhamRepository;
	}

	@Override
	@Transactional
	public CartDto xemGioHang(Long khachHangId) {
		GioHang gh = ensureCart(khachHangId);
		return toDto(gh);
	}

	@Override
	@Transactional
	public CartDto themVaoGio(Long khachHangId, CartAddItemRequest request) {
		GioHang gh = ensureCart(khachHangId);
		SanPham sp = sanPhamRepository.findById(request.getSanPhamId())
			.orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

		ChiTietGioHang existing = gh.getChiTietGioHangs()
			.stream()
			.filter(i -> i.getSanPham().getId().equals(sp.getId()))
			.findFirst()
			.orElse(null);

		if (existing == null) {
			ChiTietGioHang ct = ChiTietGioHang.builder()
				.gioHang(gh)
				.sanPham(sp)
				.soLuong(request.getSoLuong())
				.donGia(sp.getGiaBan())
				.build();
			gh.getChiTietGioHangs().add(ct);
		} else {
			existing.setSoLuong(existing.getSoLuong() + request.getSoLuong());
		}

		gh = gioHangRepository.save(gh);
		return toDto(gh);
	}

	@Override
	@Transactional
	public CartDto xoaItem(Long khachHangId, Long chiTietGioHangId) {
		GioHang gh = ensureCart(khachHangId);
		gh.getChiTietGioHangs().removeIf(i -> i.getId().equals(chiTietGioHangId));
		gh = gioHangRepository.save(gh);
		return toDto(gh);
	}

	@Override
	@Transactional
	public CartDto xoaHet(Long khachHangId) {
		GioHang gh = ensureCart(khachHangId);
		gh.getChiTietGioHangs().clear();
		gh = gioHangRepository.save(gh);
		return toDto(gh);
	}

	private GioHang ensureCart(Long khachHangId) {
		return gioHangRepository.findByKhachHangId(khachHangId)
			.orElseGet(() -> {
				KhachHang kh = khachHangRepository.findById(khachHangId)
					.orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
				GioHang gh = GioHang.builder().khachHang(kh).build();
				return gioHangRepository.save(gh);
			});
	}

	private CartDto toDto(GioHang gh) {
		List<CartItemDto> items = gh.getChiTietGioHangs().stream().map(i -> {
			BigDecimal thanhTien = i.getDonGia().multiply(BigDecimal.valueOf(i.getSoLuong()));
			return CartItemDto.builder()
				.id(i.getId())
				.sanPhamId(i.getSanPham().getId())
				.tenSanPham(i.getSanPham().getTen())
				.soLuong(i.getSoLuong())
				.donGia(i.getDonGia())
				.thanhTien(thanhTien)
				.build();
		}).toList();

		BigDecimal tong = items.stream().map(CartItemDto::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
		return CartDto.builder()
			.gioHangId(gh.getId())
			.khachHangId(gh.getKhachHang().getId())
			.items(items)
			.tongTien(tong)
			.build();
	}
}

