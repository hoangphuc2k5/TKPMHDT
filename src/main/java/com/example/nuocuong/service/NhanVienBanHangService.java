package com.example.nuocuong.service;

import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.PosOrderRequest;
import com.example.nuocuong.entity.TrangThaiDonHang;
import java.util.List;

public interface NhanVienBanHangService {
    List<DonHangResponse> getAllDonHang();
    DonHangResponse capNhatTrangThai(Long id, TrangThaiDonHang trangThai);
    DonHangResponse taoDonHangTaiQuay(PosOrderRequest request);
    String inHoaDon(Long id);
    String inPhieuGiaoHang(Long id);
}
