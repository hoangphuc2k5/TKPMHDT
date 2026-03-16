package com.example.nuocuong.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "don_hang")
public class DonHang {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 30)
	private String maDonHang;

	@ManyToOne
	@JoinColumn(name = "khach_hang_id", nullable = false)
	private KhachHang khachHang;

	@OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();

	@OneToOne(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
	private ThanhToan thanhToan;

	@ManyToOne
	@JoinColumn(name = "ma_giam_gia_id")
	private MaGiamGia maGiamGia;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TrangThaiDonHang trangThai = TrangThaiDonHang.TAO_MOI;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal tongTienHang = BigDecimal.ZERO;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal giamGia = BigDecimal.ZERO;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal tongThanhToan = BigDecimal.ZERO;

	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(length = 255)
	private String diaChiGiaoHang;
}

