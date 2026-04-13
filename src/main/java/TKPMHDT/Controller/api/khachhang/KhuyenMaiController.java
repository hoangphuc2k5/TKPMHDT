package TKPMHDT.Controller.api.khachhang;

import TKPMHDT.DTO.request.TinhTienGiamGioHangRequest;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Service.khuyenmai.KhuyenMaiService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/khuyen-mai")
public class KhuyenMaiController {

    private final KhuyenMaiService khuyenMaiService;

    public KhuyenMaiController(KhuyenMaiService khuyenMaiService) {
        this.khuyenMaiService = khuyenMaiService;
    }

    @GetMapping("/ma/{ma}")
    public ResponseEntity<MaGiamGia> timTheoMa(@PathVariable String ma) {
        return khuyenMaiService.timTheoMa(ma)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
    @GetMapping
    public ResponseEntity<List<MaGiamGia>> danhSachMa() {
        return ResponseEntity.ok(khuyenMaiService.danhSachMaGiamGia());
    }

    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
    @PostMapping
    public ResponseEntity<MaGiamGia> taoHoacCapNhat(@RequestBody MaGiamGia maGiamGia) {
        return ResponseEntity.ok(khuyenMaiService.luuMaGiamGia(maGiamGia));
    }

    /**
     * Tính giảm trên một tổng tiền (toàn đơn đã xác định là đủ điều kiện). Kiểm tra mã còn hiệu lực theo ngày hiện tại.
     */
    @PostMapping("/tinh-tien-giam")
    public ResponseEntity<?> tinhTienGiam(@RequestBody Map<String, Object> body) {
        String ma = firstString(body, "ma", "maGiamGia");
        BigDecimal tongTien = parseBigDecimal(body.get("tongTien"));

        if (ma == null || ma.isBlank() || tongTien == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cần có mã và tongTien hợp lệ"));
        }

        LocalDate homNay = LocalDate.now();
        Optional<MaGiamGia> hopLe = khuyenMaiService.timMaHopLe(ma, homNay);
        if (hopLe.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã không tồn tại hoặc không còn hiệu lực"));
        }

        BigDecimal tienGiam = khuyenMaiService.tinhTienGiam(hopLe.get(), tongTien);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("tienGiam", tienGiam);
        out.put("tongTienSauGiam", tongTien.subtract(tienGiam).max(BigDecimal.ZERO));
        out.put("giaTriGiam", tienGiam);
        return ResponseEntity.ok(out);
    }

    /**
     * Tính giảm theo từng dòng giỏ (SP + thành tiền dòng). Chỉ cộng thành tiền các dòng thuộc phạm vi mã.
     */
    @PostMapping("/tinh-cho-gio")
    public ResponseEntity<?> tinhChoGio(@RequestBody TinhTienGiamGioHangRequest req) {
        if (req == null || req.ma() == null || req.ma().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu mã"));
        }
        if (req.chiTiet() == null || req.chiTiet().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu chi tiết giỏ hàng"));
        }

        LocalDate homNay = LocalDate.now();
        Optional<MaGiamGia> hopLe = khuyenMaiService.timMaHopLe(req.ma().trim(), homNay);
        if (hopLe.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã không tồn tại hoặc không còn hiệu lực"));
        }

        MaGiamGia mg = hopLe.get();
        BigDecimal tongGoc = req.chiTiet().stream()
                .filter(d -> d != null && d.thanhTien() != null)
                .map(TinhTienGiamGioHangRequest.DongGioTinhGiam::thanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal coSo = khuyenMaiService.tongTienDuocApDungTuCacDong(mg, req.chiTiet());
        if (coSo.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mã không áp dụng cho sản phẩm trong giỏ"));
        }

        BigDecimal tienGiam = khuyenMaiService.tinhTienGiam(mg, coSo);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("tongTienGoc", tongGoc);
        out.put("tongTienDuocGiam", coSo);
        out.put("tienGiam", tienGiam);
        out.put("tongTienSauGiam", tongGoc.subtract(tienGiam).max(BigDecimal.ZERO));
        out.put("giaTriGiam", tienGiam);
        return ResponseEntity.ok(out);
    }

    private static String firstString(Map<String, Object> body, String... keys) {
        if (body == null) {
            return null;
        }
        for (String k : keys) {
            Object v = body.get(k);
            if (v instanceof String s && !s.isBlank()) {
                return s.trim();
            }
        }
        return null;
    }

    private static BigDecimal parseBigDecimal(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof BigDecimal bd) {
            return bd;
        }
        if (raw instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        try {
            return new BigDecimal(raw.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }
}
