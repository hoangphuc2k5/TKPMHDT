package com.example.nuocuong.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@DiscriminatorValue("NUOC_UONG_SAN")
public class NuocUongSan extends SanPham {
	@OneToMany(mappedBy = "nuocUongSan")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<CongThuc> congThucs = new ArrayList<>();
}

