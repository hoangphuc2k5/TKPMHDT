package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LuongNguyenLieuResponse {
    private Long nguyenLieuId;
    private String tenNguyenLieu;
    private Double soLuong;
    private String donViTinh;
}
