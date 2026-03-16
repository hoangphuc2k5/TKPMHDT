package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHangResponse {
    private Long id;
    private String maDonHang;
    private Double tongTien;
    private Double giamGia;
    private Double thanhTien;
    private String trangThai;
    private LocalDateTime ngayTao;
    private String diaChiGiaoHang;
    private List<ChiTietDonHangResponse> items;
}
