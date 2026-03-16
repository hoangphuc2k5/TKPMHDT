package com.example.nuocuong.service;

import com.example.nuocuong.dto.GioHangResponse;
import com.example.nuocuong.dto.ThemGioHangRequest;

public interface GioHangService {
    GioHangResponse getGioHang();
    void themVaoGioHang(ThemGioHangRequest request);
    void xoaKhoiGioHang(Long chiTietId);
    void capNhatSoLuong(Long chiTietId, Integer soLuong);
}
