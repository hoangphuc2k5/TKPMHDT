package com.example.nuocuong.controller;

import com.example.nuocuong.entity.LoaiGiamGia;
import com.example.nuocuong.entity.MaGiamGia;
import com.example.nuocuong.repository.MaGiamGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class MaGiamGiaController {

    private final MaGiamGiaRepository maGiamGiaRepository;

    @GetMapping("/{code}/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @PathVariable String code,
            @RequestParam(required = false, defaultValue = "0") Double amount
    ) {
        MaGiamGia coupon = maGiamGiaRepository.findByMaAndIsDeletedFalse(code)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không hợp lệ"));
        if (coupon.getNgayKetThuc() != null && coupon.getNgayKetThuc().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }
        if (coupon.getSoLuongSuDungToiDa() != null && coupon.getSoLuongDaSuDung() != null
                && coupon.getSoLuongDaSuDung() >= coupon.getSoLuongSuDungToiDa()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }
        double discount = coupon.getLoaiGiamGia() == LoaiGiamGia.PHAN_TRAM
                ? amount * coupon.getGiaTriGiam() / 100.0
                : coupon.getGiaTriGiam();
        Map<String, Object> result = new HashMap<>();
        result.put("ma", coupon.getMa());
        result.put("loaiGiamGia", coupon.getLoaiGiamGia().name());
        result.put("giaTriGiam", coupon.getGiaTriGiam());
        result.put("giamGiaTamTinh", Math.max(0, discount));
        result.put("thanhTienSauGiam", Math.max(0, amount - discount));
        return ResponseEntity.ok(result);
    }
}
