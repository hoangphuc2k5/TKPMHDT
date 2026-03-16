package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.SanPhamDto;
import com.example.nuocuong.entity.SanPham;
import com.example.nuocuong.exception.NotFoundException;
import com.example.nuocuong.repository.SanPhamRepository;
import com.example.nuocuong.service.SanPhamService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SanPhamServiceImpl implements SanPhamService {
	private final SanPhamRepository sanPhamRepository;
	private final ModelMapper modelMapper;

	public SanPhamServiceImpl(SanPhamRepository sanPhamRepository, ModelMapper modelMapper) {
		this.sanPhamRepository = sanPhamRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SanPhamDto> danhSachSanPhamDangKinhDoanh() {
		return sanPhamRepository.findAllDangKinhDoanh()
			.stream()
			.map(sp -> modelMapper.map(sp, SanPhamDto.class))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SanPhamDto chiTiet(Long id) {
		SanPham sp = sanPhamRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
		return modelMapper.map(sp, SanPhamDto.class);
	}
}

