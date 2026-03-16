package com.example.nuocuong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "ma_giam_gia")
public class MaGiamGia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 40)
	private String ma;

	@Column(length = 255)
	private String moTa;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private LoaiMaGiamGia loai;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal giaTri; // % hoặc số tiền cố định, diễn giải theo strategy

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal donToiThieu = BigDecimal.ZERO;

	@Column(nullable = false)
	private boolean kichHoat = true;

	private LocalDateTime batDauLuc;
	private LocalDateTime ketThucLuc;
}

