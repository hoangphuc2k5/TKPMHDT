package com.example.nuocuong.controller.nhanvienbanhang;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff/sales")
public class NhanVienBanHangController {
	@GetMapping
	public String dashboard() {
		return "staff/sales-dashboard";
	}
}

