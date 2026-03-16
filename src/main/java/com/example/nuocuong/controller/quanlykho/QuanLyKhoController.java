package com.example.nuocuong.controller.quanlykho;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff/warehouse")
public class QuanLyKhoController {
	@GetMapping
	public String dashboard() {
		return "staff/warehouse-dashboard";
	}
}

