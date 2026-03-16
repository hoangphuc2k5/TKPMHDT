package com.example.nuocuong.controller.nhanviengiaohang;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff/delivery")
public class NhanVienGiaoHangController {
	@GetMapping
	public String dashboard() {
		return "staff/delivery-dashboard";
	}
}

