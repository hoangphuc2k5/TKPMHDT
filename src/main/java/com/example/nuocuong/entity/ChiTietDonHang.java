package com.example.nuocuong.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "don_hang_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private DonHang donHang;

	@ManyToOne
	@JoinColumn(name = "san_pham_id", nullable = false)
	private SanPham sanPham;

	@Column(nullable = false)
	private int soLuong;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal donGia;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal thanhTien;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "tuy_chinh_id")
	private TuyChinhKhachHang tuyChinhKhachHang;
}

