package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguyenLieuResponse {
    private Long id;
    private String ten;
    private Double soLuongTon;
    private String donViTinh;
    private Double nguongCanhBao;
    private LocalDate hanSuDung;
    private String loHang;
}
