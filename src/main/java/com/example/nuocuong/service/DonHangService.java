package com.example.nuocuong.service;

import com.example.nuocuong.dto.OrderCreateRequest;
import com.example.nuocuong.dto.OrderDto;
import java.util.List;

public interface DonHangService {
	OrderDto taoDon(OrderCreateRequest request);
	List<OrderDto> lichSuDon(Long khachHangId);
	OrderDto chiTiet(Long donHangId);
}

