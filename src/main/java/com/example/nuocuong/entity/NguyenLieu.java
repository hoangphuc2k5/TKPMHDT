package com.example.nuocuong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@DiscriminatorValue("NGUYEN_LIEU")
public class NguyenLieu extends SanPham {
	// SINGLE_TABLE: cột của subclass phải nullable để insert các subtype khác (NuocUongSan) không bị lỗi NOT NULL
	@Column(nullable = true, precision = 18, scale = 3)
	private BigDecimal soLuongTon = BigDecimal.ZERO;

	@Column(nullable = true, length = 20)
	private String donViTinh = "g";

	@OneToMany(mappedBy = "nguyenLieu")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<LuongNguyenLieu> luongNguyenLieus = new ArrayList<>();
}

