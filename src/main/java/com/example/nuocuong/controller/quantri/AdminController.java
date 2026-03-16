package com.example.nuocuong.controller.quantri;

import com.example.nuocuong.repository.DonHangRepository;
import com.example.nuocuong.repository.KhachHangRepository;
import com.example.nuocuong.repository.SanPhamRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private final KhachHangRepository khachHangRepository;
	private final SanPhamRepository sanPhamRepository;
	private final DonHangRepository donHangRepository;

	public AdminController(
		KhachHangRepository khachHangRepository,
		SanPhamRepository sanPhamRepository,
		DonHangRepository donHangRepository
	) {
		this.khachHangRepository = khachHangRepository;
		this.sanPhamRepository = sanPhamRepository;
		this.donHangRepository = donHangRepository;
	}

	@GetMapping
	public String dashboard(Model model) {
		model.addAttribute("totalCustomers", khachHangRepository.count());
		model.addAttribute("totalProducts", sanPhamRepository.count());
		model.addAttribute("totalOrders", donHangRepository.count());
		return "admin/dashboard";
	}
}

