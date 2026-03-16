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
public class NhapKhoRequest {
    private Long nguyenLieuId;
    private Double soLuongThem;
    private String loHang;
    private LocalDate hanSuDung;
}
