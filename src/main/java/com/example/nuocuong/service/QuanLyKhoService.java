package com.example.nuocuong.service;

import com.example.nuocuong.dto.NguyenLieuResponse;
import com.example.nuocuong.dto.NhapKhoRequest;
import com.example.nuocuong.dto.XuatKhoRequest;
import java.util.List;

public interface QuanLyKhoService {
    List<NguyenLieuResponse> getAllNguyenLieu();
    List<NguyenLieuResponse> kiemKeTonKho();
    void nhapKho(NhapKhoRequest request);
    void xuatKho(XuatKhoRequest request);
    List<NguyenLieuResponse> getCanhBaoHetHang();
}
