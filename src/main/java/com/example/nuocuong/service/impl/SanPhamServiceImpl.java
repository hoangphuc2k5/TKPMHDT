package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.NuocUongSanResponse;
import com.example.nuocuong.dto.SanPhamResponse;
import com.example.nuocuong.entity.NuocUongSan;
import com.example.nuocuong.entity.NguyenLieu;
import com.example.nuocuong.entity.SanPham;
import com.example.nuocuong.repository.SanPhamRepository;
import com.example.nuocuong.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;

    @Override
    public List<SanPhamResponse> searchSanPham(String query, Double giaMin, Double giaMax, String loai) {
        List<SanPham> results = sanPhamRepository.search(query, giaMin, giaMax);
        
        return results.stream()
                .filter(s -> loai == null || getLoaiSanPham(s).equalsIgnoreCase(loai))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SanPhamResponse getSanPhamById(Long id) {
        SanPham s = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return mapToResponse(s);
    }

    private SanPhamResponse mapToResponse(SanPham s) {
        if (s instanceof NuocUongSan n) {
            return NuocUongSanResponse.builder()
                    .id(n.getId())
                    .ten(n.getTen())
                    .moTa(n.getMoTa())
                    .gia(n.getGia())
                    .hinhAnh(n.getHinhAnh())
                    .loaiSanPham("NUOC_UONG_SAN")
                    .dungTich(n.getDungTich())
                    .loaiNuoc(n.getLoaiNuoc())
                    .build();
        } else if (s instanceof NguyenLieu n) {
            return SanPhamResponse.builder()
                    .id(n.getId())
                    .ten(n.getTen())
                    .moTa(n.getMoTa())
                    .gia(n.getGia())
                    .hinhAnh(n.getHinhAnh())
                    .loaiSanPham("NGUYEN_LIEU")
                    .build();
        }
        return SanPhamResponse.builder()
                .id(s.getId())
                .ten(s.getTen())
                .moTa(s.getMoTa())
                .gia(s.getGia())
                .hinhAnh(s.getHinhAnh())
                .build();
    }

    private String getLoaiSanPham(SanPham s) {
        if (s instanceof NuocUongSan) return "NUOC_UONG_SAN";
        if (s instanceof NguyenLieu) return "NGUYEN_LIEU";
        return "UNKNOWN";
    }
}
