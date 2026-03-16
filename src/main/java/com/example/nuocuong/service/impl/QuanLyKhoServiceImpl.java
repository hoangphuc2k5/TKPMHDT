package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.NguyenLieuResponse;
import com.example.nuocuong.dto.NhapKhoRequest;
import com.example.nuocuong.dto.XuatKhoRequest;
import com.example.nuocuong.entity.NguyenLieu;
import com.example.nuocuong.repository.NguyenLieuRepository;
import com.example.nuocuong.service.QuanLyKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuanLyKhoServiceImpl implements QuanLyKhoService {

    private final NguyenLieuRepository nguyenLieuRepository;

    @Override
    public List<NguyenLieuResponse> getAllNguyenLieu() {
        return nguyenLieuRepository.findAll().stream()
                .filter(n -> !n.isDeleted())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NguyenLieuResponse> kiemKeTonKho() {
        return getAllNguyenLieu();
    }

    @Override
    @Transactional
    public void nhapKho(NhapKhoRequest request) {
        NguyenLieu nl = nguyenLieuRepository.findById(request.getNguyenLieuId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        
        nl.setSoLuongTon(nl.getSoLuongTon() + request.getSoLuongThem());
        nl.setLoHang(request.getLoHang());
        nl.setHanSuDung(request.getHanSuDung());
        
        nguyenLieuRepository.save(nl);
    }

    @Override
    @Transactional
    public void xuatKho(XuatKhoRequest request) {
        NguyenLieu nl = nguyenLieuRepository.findById(request.getNguyenLieuId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        double soLuongXuat = request.getSoLuongXuat() == null ? 0 : request.getSoLuongXuat();
        if (soLuongXuat <= 0) {
            throw new RuntimeException("Số lượng xuất phải lớn hơn 0");
        }
        if (nl.getSoLuongTon() < soLuongXuat) {
            throw new RuntimeException("Không đủ tồn kho để xuất");
        }
        nl.setSoLuongTon(nl.getSoLuongTon() - soLuongXuat);
        nguyenLieuRepository.save(nl);
    }

    @Override
    public List<NguyenLieuResponse> getCanhBaoHetHang() {
        return nguyenLieuRepository.findAll().stream()
                .filter(nl -> nl.getSoLuongTon() <= nl.getNguongCanhBao())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NguyenLieuResponse mapToResponse(NguyenLieu nl) {
        return NguyenLieuResponse.builder()
                .id(nl.getId())
                .ten(nl.getTen())
                .soLuongTon(nl.getSoLuongTon())
                .donViTinh(nl.getDonViTinh())
                .nguongCanhBao(nl.getNguongCanhBao())
                .hanSuDung(nl.getHanSuDung())
                .loHang(nl.getLoHang())
                .build();
    }
}
