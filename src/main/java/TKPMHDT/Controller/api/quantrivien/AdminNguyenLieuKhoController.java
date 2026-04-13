package TKPMHDT.Controller.api.quantrivien;

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

import TKPMHDT.Entity.sanpham.LichSuKho;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import TKPMHDT.Repository.sanpham.LichSuKhoRepository;
import TKPMHDT.Service.sanpham.SanPhamService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
@RequiredArgsConstructor
public class AdminNguyenLieuKhoController {

    private final SanPhamService sanPhamService;
    private final LichSuKhoRepository lichSuKhoRepository;
    private final AdminAuditSupport audit;

    @GetMapping("/nguyen-lieu")
    public ResponseEntity<List<NguyenLieu>> layDanhSachNguyenLieu(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(sanPhamService.layDanhSachNguyenLieu());
        }
        return ResponseEntity.ok(sanPhamService.timNguyenLieuTheoTen(q.trim()));
    }

    @PostMapping("/nguyen-lieu")
    public ResponseEntity<NguyenLieu> taoNguyenLieu(@RequestBody NguyenLieuRequest request) {
        NguyenLieu nguyenLieu = NguyenLieu.builder()
                .ten(request.ten())
                .donVi(request.donVi() != null ? request.donVi() : "kg")
                .soLuongTon(request.soLuongTon() != null ? request.soLuongTon() : BigDecimal.ZERO)
                .giaDonVi(request.giaDonVi() != null ? request.giaDonVi() : BigDecimal.ZERO)
                .nguongCanhBao(request.nguongCanhBao() != null ? request.nguongCanhBao() : BigDecimal.ZERO)
                .loaiNguyenLieu(request.loaiNguyenLieu() != null ? request.loaiNguyenLieu() : LoaiNguyenLieu.INGREDIENT)
                .build();
        NguyenLieu saved = sanPhamService.luuNguyenLieu(nguyenLieu);
        audit.ghiLog("NGUYEN_LIEU", "TAO", saved.getTen());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/nguyen-lieu/{id}")
    public ResponseEntity<NguyenLieu> capNhatNguyenLieu(@PathVariable UUID id, @RequestBody NguyenLieuRequest request) {
        return sanPhamService.layNguyenLieuTheoId(id).map(entity -> {
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
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/nguyen-lieu/{id}")
    public ResponseEntity<Map<String, Object>> xoaNguyenLieu(@PathVariable UUID id) {
        sanPhamService.xoaNguyenLieu(id);
        audit.ghiLog("NGUYEN_LIEU", "XOA", id.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/kho/nhap")
    public ResponseEntity<NguyenLieu> nhapKho(@RequestBody CapNhatKhoRequest request) {
        return capNhatKho(request, "NHAP");
    }

    @PostMapping("/kho/xuat")
    public ResponseEntity<NguyenLieu> xuatKho(@RequestBody CapNhatKhoRequest request) {
        return capNhatKho(request, "XUAT");
    }

    @GetMapping("/kho/canh-bao")
    public ResponseEntity<List<NguyenLieu>> layCanhBaoTonKho() {
        return ResponseEntity.ok(sanPhamService.layNguyenLieuCanhBao());
    }

    @GetMapping("/kho/lich-su")
    public ResponseEntity<List<LichSuKho>> lichSuKho() {
        return ResponseEntity.ok(lichSuKhoRepository.findTop100ByOrderByThoiGianDesc());
    }

    private ResponseEntity<NguyenLieu> capNhatKho(CapNhatKhoRequest request, String loai) {
        if (request == null || request.nguyenLieuId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn nguyên liệu");
        }
        if (request.soLuong() == null || request.soLuong().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        return sanPhamService.layNguyenLieuTheoId(request.nguyenLieuId()).map(nguyenLieu -> {
            BigDecimal soLuong = request.soLuong();
            BigDecimal tonTruoc = nguyenLieu.getSoLuongTon() == null ? BigDecimal.ZERO : nguyenLieu.getSoLuongTon();
            BigDecimal tonSau = "NHAP".equalsIgnoreCase(loai) ? tonTruoc.add(soLuong) : tonTruoc.subtract(soLuong);
            if (tonSau.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Số lượng tồn không đủ để xuất");
            }
            nguyenLieu.setSoLuongTon(tonSau);
            NguyenLieu saved = sanPhamService.luuNguyenLieu(nguyenLieu);
            LichSuKho lichSuKho = LichSuKho.builder()
                    .nguyenLieu(saved)
                    .loai(loai)
                    .soLuong(soLuong)
                    .tonTruoc(tonTruoc)
                    .tonSau(tonSau)
                    .ghiChu(request.ghiChu())
                    .nguoiThucHien(audit.tenNguoiDangNhap())
                    .build();
            lichSuKhoRepository.save(lichSuKho);
            audit.ghiLog("KHO", loai, saved.getTen() + ":" + soLuong);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
