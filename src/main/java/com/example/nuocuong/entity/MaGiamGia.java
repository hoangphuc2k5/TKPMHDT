package com.example.nuocuong.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ma_giam_gia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ma;

    private String moTa;

    @Column(nullable = false)
    private Double giaTriGiam;

    @Enumerated(EnumType.STRING)
    private LoaiGiamGia loaiGiamGia;

    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;

    private Integer soLuongSuDungToiDa;
    private Integer soLuongDaSuDung;

    @Builder.Default
    private boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;
}
