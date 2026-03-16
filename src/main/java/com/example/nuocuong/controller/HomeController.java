package com.example.nuocuong.controller;

import com.example.nuocuong.service.SanPhamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	private final SanPhamService sanPhamService;

	public HomeController(SanPhamService sanPhamService) {
		this.sanPhamService = sanPhamService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("products", sanPhamService.danhSachSanPhamDangKinhDoanh());
		return "customer/home";
	}
}

