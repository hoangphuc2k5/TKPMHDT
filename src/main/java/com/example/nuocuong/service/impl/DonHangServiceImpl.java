package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.OrderCreateRequest;
import com.example.nuocuong.dto.OrderDto;
import com.example.nuocuong.dto.OrderItemDto;
import com.example.nuocuong.dto.PaymentDto;
import com.example.nuocuong.dto.TuyChinhDto;
import com.example.nuocuong.entity.ChiTietDonHang;
import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.KhachHang;
import com.example.nuocuong.entity.MaGiamGia;
import com.example.nuocuong.entity.SanPham;
import com.example.nuocuong.entity.ThanhToan;
import com.example.nuocuong.exception.NotFoundException;
import com.example.nuocuong.factory.CustomDrinkFactory;
import com.example.nuocuong.repository.DonHangRepository;
import com.example.nuocuong.repository.KhachHangRepository;
import com.example.nuocuong.repository.SanPhamRepository;
import com.example.nuocuong.service.DonHangService;
import com.example.nuocuong.service.MaGiamGiaService;
import com.example.nuocuong.service.impl.builder.ChiTietDonHangBuilder;
import com.example.nuocuong.service.impl.builder.DonHangBuilder;
import com.example.nuocuong.strategy.StrategyRegistry;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonHangServiceImpl implements DonHangService {
	private final DonHangRepository donHangRepository;
	private final KhachHangRepository khachHangRepository;
	private final SanPhamRepository sanPhamRepository;
	private final MaGiamGiaService maGiamGiaService;
	private final StrategyRegistry strategyRegistry;
	private final CustomDrinkFactory customDrinkFactory;

	public DonHangServiceImpl(
		DonHangRepository donHangRepository,
		KhachHangRepository khachHangRepository,
		SanPhamRepository sanPhamRepository,
		MaGiamGiaService maGiamGiaService,
		StrategyRegistry strategyRegistry,
		CustomDrinkFactory customDrinkFactory
	) {
		this.donHangRepository = donHangRepository;
		this.khachHangRepository = khachHangRepository;
		this.sanPhamRepository = sanPhamRepository;
		this.maGiamGiaService = maGiamGiaService;
		this.strategyRegistry = strategyRegistry;
		this.customDrinkFactory = customDrinkFactory;
	}

	@Override
	@Transactional
	public OrderDto taoDon(OrderCreateRequest request) {
		KhachHang kh = khachHangRepository.findById(request.getKhachHangId())
			.orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));

		MaGiamGia mg = maGiamGiaService.timMaHopLe(request.getMaGiamGia());

		DonHang donHang = DonHangBuilder.builder()
			.khachHang(kh)
			.diaChiGiaoHang(request.getDiaChiGiaoHang())
			.maGiamGia(mg)
			.build();

		// Builder Pattern: dựng danh sách chi tiết theo từng bước
		for (var itemReq : request.getItems()) {
			SanPham sp = sanPhamRepository.findById(itemReq.getSanPhamId())
				.orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm id=" + itemReq.getSanPhamId()));

			var tuyChinhEntity = customDrinkFactory.taoTuyChinh(itemReq.getTuyChinh());
			BigDecimal donGia = sp.getGiaBan();

			ChiTietDonHang ct = ChiTietDonHangBuilder.builder()
				.donHang(donHang)
				.sanPham(sp)
				.soLuong(itemReq.getSoLuong())
				.donGia(donGia)
				.tuyChinh(tuyChinhEntity)
				.build();

			donHang.getChiTietDonHangs().add(ct);
		}

		BigDecimal tongTienHang = donHang.getChiTietDonHangs()
			.stream()
			.map(ChiTietDonHang::getThanhTien)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		donHang.setTongTienHang(tongTienHang);

		// Strategy Pattern: tính giảm giá theo loại mã
		BigDecimal giamGia = BigDecimal.ZERO;
		if (mg != null) {
			maGiamGiaService.validateDieuKien(tongTienHang, mg);
			giamGia = strategyRegistry.discount(mg.getLoai()).tinhGiamGia(tongTienHang, mg);
		}
		donHang.setGiamGia(giamGia);
		donHang.setTongThanhToan(tongTienHang.subtract(giamGia).max(BigDecimal.ZERO));

		// Strategy Pattern: thanh toán
		ThanhToan thanhToan = strategyRegistry.payment(request.getPhuongThucThanhToan())
			.thucHienThanhToan(donHang, donHang.getTongThanhToan());
		donHang.setThanhToan(thanhToan);

		DonHang saved = donHangRepository.save(donHang);
		return toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> lichSuDon(Long khachHangId) {
		return donHangRepository.findByKhachHangIdOrderByCreatedAtDesc(khachHangId)
			.stream()
			.map(this::toDto)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDto chiTiet(Long donHangId) {
		DonHang dh = donHangRepository.findById(donHangId)
			.orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
		return toDto(dh);
	}

	private OrderDto toDto(DonHang dh) {
		PaymentDto payment = null;
		if (dh.getThanhToan() != null) {
			ThanhToan tt = dh.getThanhToan();
			payment = PaymentDto.builder()
				.id(tt.getId())
				.phuongThuc(tt.getPhuongThuc())
				.trangThai(tt.getTrangThai())
				.soTien(tt.getSoTien())
				.maGiaoDich(tt.getMaGiaoDich())
				.thanhToanLuc(tt.getThanhToanLuc())
				.build();
		}

		List<OrderItemDto> items = dh.getChiTietDonHangs().stream().map(ct -> {
			TuyChinhDto tuyChinh = null;
			if (ct.getTuyChinhKhachHang() != null) {
				tuyChinh = TuyChinhDto.builder()
					.mucDuong(ct.getTuyChinhKhachHang().getMucDuong())
					.mucDa(ct.getTuyChinhKhachHang().getMucDa())
					.topping(ct.getTuyChinhKhachHang().getTopping())
					.ghiChu(ct.getTuyChinhKhachHang().getGhiChu())
					.build();
			}

			return OrderItemDto.builder()
				.id(ct.getId())
				.sanPhamId(ct.getSanPham().getId())
				.tenSanPham(ct.getSanPham().getTen())
				.soLuong(ct.getSoLuong())
				.donGia(ct.getDonGia())
				.thanhTien(ct.getThanhTien())
				.tuyChinh(tuyChinh)
				.build();
		}).toList();

		return OrderDto.builder()
			.id(dh.getId())
			.maDonHang(dh.getMaDonHang())
			.khachHangId(dh.getKhachHang().getId())
			.trangThai(dh.getTrangThai())
			.diaChiGiaoHang(dh.getDiaChiGiaoHang())
			.tongTienHang(dh.getTongTienHang())
			.giamGia(dh.getGiamGia())
			.tongThanhToan(dh.getTongThanhToan())
			.createdAt(dh.getCreatedAt())
			.thanhToan(payment)
			.maGiamGia(dh.getMaGiamGia() == null ? null : dh.getMaGiamGia().getMa())
			.items(items)
			.build();
	}
}

