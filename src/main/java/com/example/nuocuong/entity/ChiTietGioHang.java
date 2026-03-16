package com.example.nuocuong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "chi_tiet_gio_hang")
public class ChiTietGioHang {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "gio_hang_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private GioHang gioHang;

	@ManyToOne
	@JoinColumn(name = "san_pham_id", nullable = false)
	private SanPham sanPham;

	@Column(nullable = false)
	private int soLuong;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal donGia;
}

