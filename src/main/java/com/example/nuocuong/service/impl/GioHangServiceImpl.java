package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.ChiTietGioHangResponse;
import com.example.nuocuong.dto.GioHangResponse;
import com.example.nuocuong.dto.ThemGioHangRequest;
import com.example.nuocuong.entity.*;
import com.example.nuocuong.repository.*;
import com.example.nuocuong.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GioHangServiceImpl implements GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final CongThucRepository congThucRepository;
    private final TuyChinhKhachHangRepository tuyChinhKhachHangRepository;

    @Override
    public GioHangResponse getGioHang() {
        GioHang gh = getCurrentGioHang();
        return mapToResponse(gh);
    }

    @Override
    @Transactional
    public void themVaoGioHang(ThemGioHangRequest request) {
        GioHang gh = getCurrentGioHang();
        SanPham sp = sanPhamRepository.findById(request.getSanPhamId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        TuyChinhKhachHang tuyChinh = null;
        if (request.getTuyChinh() != null) {
            CongThuc ct = congThucRepository.findById(request.getTuyChinh().getCongThucId())
                    .orElseThrow(() -> new RuntimeException("Công thức không tồn tại"));
            
            tuyChinh = TuyChinhKhachHang.builder()
                    .khachHang(gh.getKhachHang())
                    .congThuc(ct)
                    .tuyChinh(request.getTuyChinh().getTuyChinh())
                    .build();
            tuyChinh = tuyChinhKhachHangRepository.save(tuyChinh);
        }

        ChiTietGioHang item = ChiTietGioHang.builder()
                .gioHang(gh)
                .sanPham(sp)
                .soLuong(request.getSoLuong())
                .tuyChinh(tuyChinh)
                .giaTaiThoiDiem(sp.getGia())
                .build();
        
        chiTietGioHangRepository.save(item);
    }

    @Override
    @Transactional
    public void xoaKhoiGioHang(Long chiTietId) {
        chiTietGioHangRepository.deleteById(chiTietId);
    }

    @Override
    @Transactional
    public void capNhatSoLuong(Long chiTietId, Integer soLuong) {
        ChiTietGioHang item = chiTietGioHangRepository.findById(chiTietId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));
        item.setSoLuong(soLuong);
        chiTietGioHangRepository.save(item);
    }

    private GioHang getCurrentGioHang() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        KhachHang kh = khachHangRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        
        return gioHangRepository.findByKhachHang(kh)
                .orElseGet(() -> {
                    GioHang newGh = GioHang.builder().khachHang(kh).danhSachChiTiet(new ArrayList<>()).build();
                    return gioHangRepository.save(newGh);
                });
    }

    private GioHangResponse mapToResponse(GioHang gh) {
        var items = gh.getDanhSachChiTiet().stream()
                .map(item -> ChiTietGioHangResponse.builder()
                        .id(item.getId())
                        .sanPhamId(item.getSanPham().getId())
                        .tenSanPham(item.getSanPham().getTen())
                        .gia(item.getGiaTaiThoiDiem())
                        .soLuong(item.getSoLuong())
                        .tuyChinh(item.getTuyChinh() != null ? item.getTuyChinh().getTuyChinh() : null)
                        .thanhTien(item.getGiaTaiThoiDiem() * item.getSoLuong())
                        .build())
                .collect(Collectors.toList());
        
        double tongTien = items.stream().mapToDouble(ChiTietGioHangResponse::getThanhTien).sum();
        
        return GioHangResponse.builder()
                .items(items)
                .tongTien(tongTien)
                .build();
    }
}
