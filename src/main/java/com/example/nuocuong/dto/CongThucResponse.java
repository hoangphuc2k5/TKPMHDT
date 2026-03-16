package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongThucResponse {
    private Long id;
    private String ten;
    private String moTa;
    private List<LuongNguyenLieuResponse> danhSachNguyenLieu;
}
