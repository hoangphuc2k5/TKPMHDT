package com.example.nuocuong.service;

import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.ThanhToanRequest;
import java.util.List;

public interface DonHangService {
    DonHangResponse thanhToan(ThanhToanRequest request);
    List<DonHangResponse> getLichSuDonHang();
    DonHangResponse getChiTietDonHang(Long id);
    void huyDonHang(Long id);
}
