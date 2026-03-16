package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangResponse {
    private String tenSanPham;
    private Double gia;
    private Integer soLuong;
    private String tuyChinh;
    private Double thanhTien;
}
