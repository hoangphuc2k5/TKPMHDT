package com.example.nuocuong.controller;

import com.example.nuocuong.dto.CongThucResponse;
import com.example.nuocuong.service.CongThucService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/formulas")
@RequiredArgsConstructor
public class CongThucController {

    private final CongThucService congThucService;

    @GetMapping
    public ResponseEntity<List<CongThucResponse>> getAll() {
        return ResponseEntity.ok(congThucService.getAllCongThuc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CongThucResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(congThucService.getCongThucById(id));
    }
}
