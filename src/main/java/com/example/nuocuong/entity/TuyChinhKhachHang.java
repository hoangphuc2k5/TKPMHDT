package com.example.nuocuong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "tuy_chinh_khach_hang")
public class TuyChinhKhachHang {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Strategy/Factory sẽ diễn giải các lựa chọn này khi tạo đồ uống đặt
	@Column(nullable = false)
	private int mucDuong = 100; // %

	@Column(nullable = false)
	private int mucDa = 100; // %

	@Column(length = 255)
	private String topping; // ví dụ: "tranChau,thachPhoMai"

	@Column(length = 255)
	private String ghiChu;
}

