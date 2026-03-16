package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.CongThucResponse;
import com.example.nuocuong.dto.LuongNguyenLieuResponse;
import com.example.nuocuong.entity.CongThuc;
import com.example.nuocuong.repository.CongThucRepository;
import com.example.nuocuong.service.CongThucService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CongThucServiceImpl implements CongThucService {

    private final CongThucRepository congThucRepository;

    @Override
    public List<CongThucResponse> getAllCongThuc() {
        return congThucRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CongThucResponse getCongThucById(Long id) {
        CongThuc c = congThucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));
        return mapToResponse(c);
    }

    private CongThucResponse mapToResponse(CongThuc c) {
        return CongThucResponse.builder()
                .id(c.getId())
                .ten(c.getTen())
                .moTa(c.getMoTa())
                .danhSachNguyenLieu(c.getDanhSachNguyenLieu().stream()
                        .map(lnl -> LuongNguyenLieuResponse.builder()
                                .nguyenLieuId(lnl.getNguyenLieu().getId())
                                .tenNguyenLieu(lnl.getNguyenLieu().getTen())
                                .soLuong(lnl.getSoLuong())
                                .donViTinh(lnl.getNguyenLieu().getDonViTinh())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
