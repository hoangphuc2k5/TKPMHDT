package com.example.nuocuong.controller;

import com.example.nuocuong.dto.SanPhamResponse;
import com.example.nuocuong.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @GetMapping("/search")
    public ResponseEntity<List<SanPhamResponse>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Double giaMin,
            @RequestParam(required = false) Double giaMax,
            @RequestParam(required = false) String loai
    ) {
        return ResponseEntity.ok(sanPhamService.searchSanPham(query, giaMin, giaMax, loai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sanPhamService.getSanPhamById(id));
    }
}
