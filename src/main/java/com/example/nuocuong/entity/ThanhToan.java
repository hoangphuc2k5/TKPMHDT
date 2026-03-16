package com.example.nuocuong.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @Enumerated(EnumType.STRING)
    private PhuongThucThanhToan phuongThuc;

    @Enumerated(EnumType.STRING)
    private TrangThaiThanhToan trangThai;

    private Double soTien;
    private String maGiaoDich;

    @CreationTimestamp
    private LocalDateTime ngayThanhToan;
}
