package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XuatKhoRequest {
    private Long nguyenLieuId;
    private Double soLuongXuat;
    private String ghiChu;
}
