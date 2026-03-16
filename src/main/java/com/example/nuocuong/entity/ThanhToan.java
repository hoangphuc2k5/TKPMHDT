package com.example.nuocuong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "thanh_toan")
public class ThanhToan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "don_hang_id", nullable = false, unique = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private DonHang donHang;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private PhuongThucThanhToan phuongThuc;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TrangThaiThanhToan trangThai = TrangThaiThanhToan.CHO_THANH_TOAN;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal soTien;

	@Column(length = 80)
	private String maGiaoDich;

	private LocalDateTime thanhToanLuc;
}

