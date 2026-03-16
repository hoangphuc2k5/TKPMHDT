package com.example.nuocuong.dto;

import com.example.nuocuong.entity.VaiTro;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NguoiDungDto {
	Long id;
	String email;
	String hoTen;
	VaiTro vaiTro;
	boolean kichHoat;
}

