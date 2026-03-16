package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.ChiTietDonHangResponse;
import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.ThanhToanRequest;
import com.example.nuocuong.entity.*;
import com.example.nuocuong.repository.*;
import com.example.nuocuong.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonHangServiceImpl implements DonHangService {

    private final DonHangRepository donHangRepository;
    private final GioHangRepository gioHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final ThanhToanRepository thanhToanRepository;

    @Override
    @Transactional
    public DonHangResponse thanhToan(ThanhToanRequest request) {
        KhachHang kh = getCurrentKhachHang();
        GioHang gh = gioHangRepository.findByKhachHang(kh)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống"));

        if (gh.getDanhSachChiTiet().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        double tongTien = gh.getDanhSachChiTiet().stream()
                .mapToDouble(item -> item.getGiaTaiThoiDiem() * item.getSoLuong())
                .sum();

        double giamGia = 0;
        if (request.getMaGiamGia() != null && !request.getMaGiamGia().isEmpty()) {
            MaGiamGia ma = maGiamGiaRepository.findByMaAndIsDeletedFalse(request.getMaGiamGia())
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không hợp lệ"));
            
            if (ma.getNgayKetThuc().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Mã giảm giá đã hết hạn");
            }
            if (ma.getSoLuongDaSuDung() >= ma.getSoLuongSuDungToiDa()) {
                throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
            }

            if (ma.getLoaiGiamGia() == LoaiGiamGia.PHAN_TRAM) {
                giamGia = tongTien * (ma.getGiaTriGiam() / 100.0);
            } else {
                giamGia = ma.getGiaTriGiam();
            }
            ma.setSoLuongDaSuDung(ma.getSoLuongDaSuDung() + 1);
            maGiamGiaRepository.save(ma);
        }

        DonHang dh = DonHang.builder()
                .maDonHang("DH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .khachHang(kh)
                .tongTien(tongTien)
                .giamGia(giamGia)
                .thanhTien(Math.max(0, tongTien - giamGia))
                .trangThai(TrangThaiDonHang.MOI_TAO)
                .diaChiGiaoHang(request.getDiaChiGiaoHang())
                .soDienThoaiGiaoHang(request.getSoDienThoaiGiaoHang())
                .ghiChu(request.getGhiChu())
                .build();

        List<ChiTietDonHang> items = gh.getDanhSachChiTiet().stream()
                .map(item -> ChiTietDonHang.builder()
                        .donHang(dh)
                        .sanPham(item.getSanPham())
                        .soLuong(item.getSoLuong())
                        .tuyChinh(item.getTuyChinh())
                        .giaTaiThoiDiem(item.getGiaTaiThoiDiem())
                        .build())
                .collect(Collectors.toList());
        
        dh.setDanhSachChiTiet(items);
        donHangRepository.save(dh);

        // Mô phỏng thanh toán
        ThanhToan tt = ThanhToan.builder()
                .donHang(dh)
                .phuongThuc(PhuongThucThanhToan.valueOf(request.getPhuongThucThanhToan()))
                .trangThai(TrangThaiThanhToan.DA_THANH_TOAN) // Giả định thanh toán thành công
                .soTien(dh.getThanhTien())
                .maGiaoDich("TRANS" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .build();
        thanhToanRepository.save(tt);

        // Xóa giỏ hàng
        gh.getDanhSachChiTiet().clear();
        gioHangRepository.save(gh);

        return mapToResponse(dh);
    }

    @Override
    public List<DonHangResponse> getLichSuDonHang() {
        KhachHang kh = getCurrentKhachHang();
        return donHangRepository.findByKhachHang(kh).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DonHangResponse getChiTietDonHang(Long id) {
        DonHang dh = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return mapToResponse(dh);
    }

    @Override
    @Transactional
    public void huyDonHang(Long id) {
        DonHang dh = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        if (dh.getTrangThai() != TrangThaiDonHang.MOI_TAO && dh.getTrangThai() != TrangThaiDonHang.CHO_XU_LY) {
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái này");
        }
        dh.setTrangThai(TrangThaiDonHang.DA_HUY);
        donHangRepository.save(dh);
    }

    private KhachHang getCurrentKhachHang() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return khachHangRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    private DonHangResponse mapToResponse(DonHang dh) {
        return DonHangResponse.builder()
                .id(dh.getId())
                .maDonHang(dh.getMaDonHang())
                .tongTien(dh.getTongTien())
                .giamGia(dh.getGiamGia())
                .thanhTien(dh.getThanhTien())
                .trangThai(dh.getTrangThai().name())
                .ngayTao(dh.getNgayTao())
                .diaChiGiaoHang(dh.getDiaChiGiaoHang())
                .items(dh.getDanhSachChiTiet().stream()
                        .map(item -> ChiTietDonHangResponse.builder()
                                .tenSanPham(item.getSanPham().getTen())
                                .gia(item.getGiaTaiThoiDiem())
                                .soLuong(item.getSoLuong())
                                .tuyChinh(item.getTuyChinh() != null ? item.getTuyChinh().getTuyChinh() : null)
                                .thanhTien(item.getGiaTaiThoiDiem() * item.getSoLuong())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
