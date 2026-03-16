package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietGioHangResponse {
    private Long id;
    private Long sanPhamId;
    private String tenSanPham;
    private Double gia;
    private Integer soLuong;
    private String tuyChinh; // Mô tả tùy chỉnh (nếu có)
    private Double thanhTien;
}
