package com.example.nuocuong.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@DiscriminatorValue("KHACH_HANG")
public class KhachHang extends NguoiDung {
	@Column(nullable = false, length = 120)
	private String hoTen;

	@Column(length = 20)
	private String soDienThoai;

	private LocalDate ngaySinh;

	@Column(length = 255)
	private String diaChiMacDinh;

	@OneToOne(mappedBy = "khachHang", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private GioHang gioHang;

	@OneToMany(mappedBy = "khachHang")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<DonHang> donHangs = new ArrayList<>();
}

