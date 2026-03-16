package com.example.nuocuong.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamResponse {
    private Long id;
    private String ten;
    private String moTa;
    private Double gia;
    private String hinhAnh;
    private String loaiSanPham; // "NUOC_UONG_SAN" hoặc "NGUYEN_LIEU"
}
