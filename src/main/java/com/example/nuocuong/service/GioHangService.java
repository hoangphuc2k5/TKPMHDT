package com.example.nuocuong.service;

import com.example.nuocuong.dto.CartAddItemRequest;
import com.example.nuocuong.dto.CartDto;

public interface GioHangService {
	CartDto xemGioHang(Long khachHangId);
	CartDto themVaoGio(Long khachHangId, CartAddItemRequest request);
	CartDto xoaItem(Long khachHangId, Long chiTietGioHangId);
	CartDto xoaHet(Long khachHangId);
}

