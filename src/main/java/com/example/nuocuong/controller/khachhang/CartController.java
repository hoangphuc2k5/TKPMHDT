package com.example.nuocuong.controller.khachhang;

import com.example.nuocuong.dto.CartAddItemRequest;
import com.example.nuocuong.service.GioHangService;
import jakarta.validation.Valid;
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
@RequestMapping("/customer/cart")
public class CartController {
	private final GioHangService gioHangService;

	public CartController(GioHangService gioHangService) {
		this.gioHangService = gioHangService;
	}

	@GetMapping
	public String view(@RequestParam(defaultValue = "1") Long khachHangId, Model model) {
		// Demo: lấy khachHangId từ query param (khi tích hợp Security sẽ lấy từ principal)
		model.addAttribute("cart", gioHangService.xemGioHang(khachHangId));
		model.addAttribute("khachHangId", khachHangId);
		model.addAttribute("addForm", CartAddItemRequest.builder().build());
		return "customer/cart";
	}

	@PostMapping("/add")
	public String add(@RequestParam Long khachHangId, @Valid @ModelAttribute("addForm") CartAddItemRequest form, BindingResult br, Model model) {
		if (br.hasErrors()) {
			model.addAttribute("cart", gioHangService.xemGioHang(khachHangId));
			model.addAttribute("khachHangId", khachHangId);
			return "customer/cart";
		}
		gioHangService.themVaoGio(khachHangId, form);
		return "redirect:/customer/cart?khachHangId=" + khachHangId;
	}

	@PostMapping("/remove/{itemId}")
	public String remove(@RequestParam Long khachHangId, @PathVariable("itemId") Long itemId) {
		gioHangService.xoaItem(khachHangId, itemId);
		return "redirect:/customer/cart?khachHangId=" + khachHangId;
	}

	@PostMapping("/clear")
	public String clear(@RequestParam Long khachHangId) {
		gioHangService.xoaHet(khachHangId);
		return "redirect:/customer/cart?khachHangId=" + khachHangId;
	}
}

