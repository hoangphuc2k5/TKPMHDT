package com.example.nuocuong.controller.khachhang;

import com.example.nuocuong.dto.AuthRegisterRequest;
import com.example.nuocuong.dto.OtpVerifyRequest;
import com.example.nuocuong.service.AuthService;
import com.example.nuocuong.service.FakeMailService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;
	private final FakeMailService fakeMailService;

	public AuthController(AuthService authService, FakeMailService fakeMailService) {
		this.authService = authService;
		this.fakeMailService = fakeMailService;
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("form", AuthRegisterRequest.builder().build());
		return "auth/register";
	}

	@PostMapping("/register")
	public String registerSubmit(@Valid @ModelAttribute("form") AuthRegisterRequest form, BindingResult br, Model model) {
		if (br.hasErrors()) return "auth/register";
		authService.dangKyKhachHang(form);
		model.addAttribute("email", form.getEmail());
		model.addAttribute("otpForm", OtpVerifyRequest.builder().email(form.getEmail()).build());
		return "auth/verify-otp";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@Valid @ModelAttribute("otpForm") OtpVerifyRequest form, BindingResult br, Model model) {
		if (br.hasErrors()) {
			model.addAttribute("email", form.getEmail());
			return "auth/verify-otp";
		}
		authService.xacThucOtpVaKichHoat(form);
		return "redirect:/auth/login?verified";
	}

	@GetMapping("/otp-inbox")
	public String otpInbox(Model model) {
		model.addAttribute("mails", fakeMailService.inbox());
		return "auth/otp-inbox";
	}
}

