package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToanRequest {
    private String phuongThucThanhToan; // TIEN_MAT, CHUYEN_KHOAN, VI_DIEN_TU
    private String maGiamGia;
    private String diaChiGiaoHang;
    private String soDienThoaiGiaoHang;
    private String ghiChu;
}
