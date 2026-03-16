package com.example.nuocuong.controller.khachhang;

import com.example.nuocuong.service.SanPhamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer/products")
public class ProductController {
	private final SanPhamService sanPhamService;

	public ProductController(SanPhamService sanPhamService) {
		this.sanPhamService = sanPhamService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("products", sanPhamService.danhSachSanPhamDangKinhDoanh());
		return "customer/products";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("product", sanPhamService.chiTiet(id));
		return "customer/product-detail";
	}
}

