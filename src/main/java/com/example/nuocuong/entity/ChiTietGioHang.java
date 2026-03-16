package com.example.nuocuong.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_gio_hang")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietGioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gio_hang_id")
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham; // Có thể là NuocUongSan hoặc NguyenLieu (nếu bán lẻ)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuy_chinh_id")
    private TuyChinhKhachHang tuyChinh; // Dành cho đồ uống tùy chỉnh

    @Column(nullable = false)
    private Integer soLuong;

    private Double giaTaiThoiDiem;
}
