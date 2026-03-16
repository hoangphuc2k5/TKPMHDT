package com.example.nuocuong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "loai_san_pham")
public abstract class SanPham {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 160)
	private String ten;

	@Column(length = 255)
	private String moTa;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal giaBan;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private LoaiSanPham loai;

	@Column(length = 255)
	private String hinhAnhUrl;

	@Column(nullable = false)
	private boolean dangKinhDoanh = true;
}

