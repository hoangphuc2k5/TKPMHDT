package com.example.nuocuong.controller;

import com.example.nuocuong.exception.BusinessException;
import com.example.nuocuong.exception.NotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(NotFoundException.class)
	public String notFound(NotFoundException ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "error/404";
	}

	@ExceptionHandler(BusinessException.class)
	public String business(BusinessException ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "error/400";
	}

	@ExceptionHandler(Exception.class)
	public String other(Exception ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "error/500";
	}
}

