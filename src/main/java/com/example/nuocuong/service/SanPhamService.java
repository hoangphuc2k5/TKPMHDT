package com.example.nuocuong.service;

import com.example.nuocuong.dto.SanPhamResponse;
import java.util.List;

public interface SanPhamService {
    List<SanPhamResponse> searchSanPham(String query, Double giaMin, Double giaMax, String loai);
    SanPhamResponse getSanPhamById(Long id);
}
