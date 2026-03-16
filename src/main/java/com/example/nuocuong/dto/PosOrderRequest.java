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
public class PosOrderRequest {
    private String phuongThucThanhToan;
    private String maGiamGia;
    private String tenKhach;
    private String soDienThoai;
    private String ghiChu;
    private List<PosItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PosItemRequest {
        private Long sanPhamId;
        private Integer soLuong;
        private String tuyChinh;
    }
}
