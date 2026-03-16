package com.example.nuocuong.service;

import com.example.nuocuong.dto.SanPhamDto;
import java.util.List;

public interface SanPhamService {
	List<SanPhamDto> danhSachSanPhamDangKinhDoanh();
	SanPhamDto chiTiet(Long id);
}

