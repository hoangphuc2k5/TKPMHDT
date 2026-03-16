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
@Table(name = "luong_nguyen_lieu")
public class LuongNguyenLieu {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "cong_thuc_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CongThuc congThuc;

	@ManyToOne
	@JoinColumn(name = "nguyen_lieu_id", nullable = false)
	private NguyenLieu nguyenLieu;

	@Column(nullable = false, precision = 18, scale = 3)
	private BigDecimal dinhLuong;
}

