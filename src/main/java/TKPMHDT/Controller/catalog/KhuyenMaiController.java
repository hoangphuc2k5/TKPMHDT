package TKPMHDT.Controller.catalog;

import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Service.khuyenmai.KhuyenMaiService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/tinh-tien-giam")
    public ResponseEntity<?> tinhTienGiam(@RequestBody Map<String, String> body) {
        String ma = body.get("ma");
        String tongTienRaw = body.get("tongTien");

        if (ma == null || ma.isBlank() || tongTienRaw == null || tongTienRaw.isBlank()) {
            return ResponseEntity.badRequest().body("ma and tongTien are required");
        }

        BigDecimal tongTien = new BigDecimal(tongTienRaw);
        return khuyenMaiService.timTheoMa(ma)
                .map(mg -> ResponseEntity.ok(khuyenMaiService.tinhTienGiam(mg, tongTien)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

