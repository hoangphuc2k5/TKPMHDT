package com.example.nuocuong.controller.khachhang;

import com.example.nuocuong.dto.OrderCreateRequest;
import com.example.nuocuong.entity.PhuongThucThanhToan;
import com.example.nuocuong.service.DonHangService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/customer/orders")
public class OrderController {
	private final DonHangService donHangService;

	public OrderController(DonHangService donHangService) {
		this.donHangService = donHangService;
	}

	@GetMapping
	public String history(@RequestParam(defaultValue = "1") Long khachHangId, Model model) {
		model.addAttribute("orders", donHangService.lichSuDon(khachHangId));
		model.addAttribute("khachHangId", khachHangId);
		return "customer/orders";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("order", donHangService.chiTiet(id));
		return "customer/order-detail";
	}

	@GetMapping("/checkout")
	public String checkoutForm(@RequestParam(defaultValue = "1") Long khachHangId, Model model) {
		model.addAttribute("khachHangId", khachHangId);
		model.addAttribute("paymentMethods", PhuongThucThanhToan.values());
		model.addAttribute("form", OrderCreateRequest.builder()
			.khachHangId(khachHangId)
			.phuongThucThanhToan(PhuongThucThanhToan.COD)
			.items(new ArrayList<>())
			.build());
		return "customer/checkout";
	}

	@PostMapping("/checkout")
	public String checkoutSubmit(@Valid @ModelAttribute("form") OrderCreateRequest form, BindingResult br, Model model) {
		if (br.hasErrors()) {
			model.addAttribute("paymentMethods", PhuongThucThanhToan.values());
			return "customer/checkout";
		}
		var order = donHangService.taoDon(form);
		return "redirect:/customer/orders/" + order.getId();
	}
}

