package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminNguyenLieuController {

    private final SanPhamService sanPhamService;
    private final AdminAuditHelper audit;

    public AdminNguyenLieuController(SanPhamService sanPhamService, AdminAuditHelper audit) {
        this.sanPhamService = sanPhamService;
        this.audit = audit;
    }

    @GetMapping("/nguyen-lieu")
    public ResponseEntity<List<NguyenLieu>> layDanhSachNguyenLieu(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(sanPhamService.layDanhSachNguyenLieu());
        }
        return ResponseEntity.ok(sanPhamService.timNguyenLieuTheoTen(q.trim()));
    }

    @PostMapping("/nguyen-lieu")
    public ResponseEntity<NguyenLieu> taoNguyenLieu(@RequestBody AdminApiDtos.NguyenLieuRequest request) {
        NguyenLieu nguyenLieu = NguyenLieu.builder()
                .ten(request.ten())
                .donVi(request.donVi() != null ? request.donVi() : "kg")
                .soLuongTon(request.soLuongTon() != null ? request.soLuongTon() : BigDecimal.ZERO)
                .giaDonVi(request.giaDonVi() != null ? request.giaDonVi() : BigDecimal.ZERO)
                .nguongCanhBao(request.nguongCanhBao() != null ? request.nguongCanhBao() : BigDecimal.ZERO)
                .loaiNguyenLieu(
                        request.loaiNguyenLieu() != null ? request.loaiNguyenLieu() : LoaiNguyenLieu.INGREDIENT)
                .build();
        NguyenLieu saved = sanPhamService.luuNguyenLieu(nguyenLieu);
        audit.ghiLog("NGUYEN_LIEU", "TAO", saved.getTen());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/nguyen-lieu/{id}")
    public ResponseEntity<NguyenLieu> capNhatNguyenLieu(
            @PathVariable UUID id, @RequestBody AdminApiDtos.NguyenLieuRequest request) {
        return sanPhamService.layNguyenLieuTheoId(id)
                .map(entity -> {
                    if (request.ten() != null) {
                        entity.setTen(request.ten());
                    }
                    if (request.donVi() != null) {
                        entity.setDonVi(request.donVi());
                    }
                    if (request.soLuongTon() != null) {
                        entity.setSoLuongTon(request.soLuongTon());
                    }
                    if (request.giaDonVi() != null) {
                        entity.setGiaDonVi(request.giaDonVi());
                    }
                    if (request.nguongCanhBao() != null) {
                        entity.setNguongCanhBao(request.nguongCanhBao());
                    }
                    if (request.loaiNguyenLieu() != null) {
                        entity.setLoaiNguyenLieu(request.loaiNguyenLieu());
                    }
                    NguyenLieu updated = sanPhamService.luuNguyenLieu(entity);
                    audit.ghiLog("NGUYEN_LIEU", "CAP_NHAT", updated.getTen());
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/nguyen-lieu/{id}")
    public ResponseEntity<Map<String, Object>> xoaNguyenLieu(@PathVariable UUID id) {
        sanPhamService.xoaNguyenLieu(id);
        audit.ghiLog("NGUYEN_LIEU", "XOA", id.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
