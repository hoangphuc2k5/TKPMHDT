package com.example.nuocuong.controller;

import com.example.nuocuong.dto.GioHangResponse;
import com.example.nuocuong.dto.ThemGioHangRequest;
import com.example.nuocuong.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class GioHangController {

    private final GioHangService gioHangService;

    @GetMapping
    public ResponseEntity<GioHangResponse> get() {
        return ResponseEntity.ok(gioHangService.getGioHang());
    }

    @PostMapping("/add")
    public ResponseEntity<Void> add(@RequestBody ThemGioHangRequest request) {
        gioHangService.themVaoGioHang(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{chiTietId}")
    public ResponseEntity<Void> remove(@PathVariable Long chiTietId) {
        gioHangService.xoaKhoiGioHang(chiTietId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{chiTietId}")
    public ResponseEntity<Void> update(@PathVariable Long chiTietId, @RequestParam Integer soLuong) {
        gioHangService.capNhatSoLuong(chiTietId, soLuong);
        return ResponseEntity.ok().build();
    }
}
