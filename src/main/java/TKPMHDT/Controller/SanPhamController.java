package TKPMHDT.Controller;

import TKPMHDT.DTO.response.NuocUongHienThiKhachHang;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import TKPMHDT.Service.FileStorageService;
import TKPMHDT.Service.sanpham.SanPhamService;
import TKPMHDT.Service.sanpham.TuyChinhSanPhamService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * SanPhamController - Quản lý sản phẩm
 * UC05: Tìm kiếm & Duyệt sản phẩm
 * UC06: Xem chi tiết sản phẩm
 * UC07: Tùy chỉnh sản phẩm
 */
@RestController
@RequestMapping("/api/san-pham")
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final TuyChinhSanPhamService tuyChinhSanPhamService;
    private final FileStorageService fileStorageService;

    public SanPhamController(
            SanPhamService sanPhamService,
            TuyChinhSanPhamService tuyChinhSanPhamService,
            FileStorageService fileStorageService) {
        this.sanPhamService = sanPhamService;
        this.tuyChinhSanPhamService = tuyChinhSanPhamService;
        this.fileStorageService = fileStorageService;
    }

    // ==================== UC05-UC06: Tìm kiếm & Duyệt & Chi tiết ====================

    /**
     * UC05: Lấy danh sách sản phẩm (Duyệt sản phẩm)
     */
    @GetMapping("/nuoc-uong")
    public ResponseEntity<List<NuocUongHienThiKhachHang>> layDanhSachNuocUong() {
        return ResponseEntity.ok(sanPhamService.layDanhSachNuocUongChoKhachHang());
    }

    /**
     * UC05: Tìm kiếm sản phẩm theo tên
     */
    @GetMapping("/nuoc-uong/tim")
    public ResponseEntity<List<NuocUongSan>> timNuocUong(@RequestParam String ten) {
        return ResponseEntity.ok(sanPhamService.timNuocUongTheoTen(ten));
    }

    /**
     * UC06: Xem chi tiết sản phẩm
     */
    @GetMapping("/nuoc-uong/{id}")
    public ResponseEntity<NuocUongHienThiKhachHang> layNuocUongTheoId(@PathVariable UUID id) {
        return sanPhamService.layMotNuocChoKhachHang(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * UC06 (Nâng cao): Xem chi tiết sản phẩm đầy đủ
     * Trả về:
     *   + Thông tin sản phẩm
     *   + Các tùy chọn: đá, topping
     * Sử dụng Singleton Pattern trong Service
     */
    @GetMapping("/nuoc-uong/{id}/detail")
    public ResponseEntity<?> layChiTietDayDu(@PathVariable UUID id) {
        return ResponseEntity.ok(sanPhamService.layChiTietDayDu(id));
    }

    // ==================== UC07: Tùy chỉnh sản phẩm ====================

    /**
     * UC07: Kiểm tra sản phẩm có thể tùy chỉnh không
     */
    @GetMapping("/{sanPhamId}/co-the-tuy-chinh")
    public ResponseEntity<Map<String, Object>> kiemTraCoTheTuyChinh(@PathVariable UUID sanPhamId) {
        try {
            boolean coTheTuyChinh = tuyChinhSanPhamService.coTheTuyChinh(sanPhamId);
            return ResponseEntity.ok(Map.of(
                    "sanPhamId", sanPhamId,
                    "coTheTuyChinh", coTheTuyChinh
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * UC07: Lấy danh sách nguyên liệu có thể thêm vào sản phẩm
     */
    @GetMapping("/{sanPhamId}/nguyen-lieu-them")
    public ResponseEntity<List<NguyenLieu>> layNguyenLieuCoTheThem(@PathVariable UUID sanPhamId) {
        return ResponseEntity.ok(tuyChinhSanPhamService.layDanhSachNguyenLieuThem(sanPhamId));
    }

    /**
     * UC07: Tạo cấu hình tùy chỉnh sản phẩm
     */
    @PreAuthorize("hasAuthority('product:customize')")
    @PostMapping("/{sanPhamId}/tuy-chinh")
    public ResponseEntity<Map<String, Object>> taoCauHinhTuyChinh(
            @PathVariable UUID sanPhamId,
            @RequestBody TuyChinhRequest request) {
        try {
            TuyChinhKhachHang tuyChinh = tuyChinhSanPhamService.taoTuyChinh(
                    request.mucDa(),
                    request.ghiChu()
            );

            // Thêm nguyên liệu nếu có
            if (request.nguyenLieuThem() != null) {
                for (NguyenLieuThemRequest nlThem : request.nguyenLieuThem()) {
                    tuyChinhSanPhamService.themNguyenLieuTuyChinh(tuyChinh, nlThem.nguyenLieuId(), nlThem.soLuong());
                }
            }

            // Tính giá cuối cùng
            BigDecimal giaCuoiCung = tuyChinhSanPhamService.tinhGiaCuoiCung(sanPhamId, tuyChinh);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tuyChinh", tuyChinh);
            response.put("giaCuoiCung", giaCuoiCung);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ==================== Admin: Quản lý sản phẩm ====================

    @PreAuthorize("hasAuthority('product:manage')")
    @PostMapping("/nuoc-uong")
    public ResponseEntity<NuocUongSan> luuNuocUong(@RequestBody NuocUongSan nuocUongSan) {
        return ResponseEntity.ok(sanPhamService.luuNuocUong(nuocUongSan));
    }

    @PreAuthorize("hasAuthority('product:manage')")
    @PostMapping("/nuoc-uong/{id}/hinh-anh")
    public ResponseEntity<NuocUongSan> uploadHinhAnh(
            @PathVariable UUID id,
            @RequestParam("hinhAnh") MultipartFile[] files) {
        return sanPhamService.layNuocUongTheoId(id).map(nuocUong -> {
            List<String> hinhAnhUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = fileStorageService.storeFile(file);
                hinhAnhUrls.add(url);
            }
            nuocUong.getHinhAnh().addAll(hinhAnhUrls);
            return ResponseEntity.ok(sanPhamService.luuNuocUong(nuocUong));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nguyen-lieu")
    public ResponseEntity<List<NguyenLieu>> layDanhSachNguyenLieu() {
        return ResponseEntity.ok(sanPhamService.layDanhSachNguyenLieu());
    }

    @GetMapping("/nguyen-lieu/tim")
    public ResponseEntity<List<NguyenLieu>> timNguyenLieu(@RequestParam String ten) {
        return ResponseEntity.ok(sanPhamService.timNguyenLieuTheoTen(ten));
    }

    @PreAuthorize("hasAuthority('product:manage')")
    @PostMapping("/nguyen-lieu")
    public ResponseEntity<NguyenLieu> luuNguyenLieu(@RequestBody NguyenLieu nguyenLieu) {
        return ResponseEntity.ok(sanPhamService.luuNguyenLieu(nguyenLieu));
    }

    //Lấy danh sách nguyên liệu là topping
   

    // ==================== Request Classes ====================

    public record TuyChinhRequest(
            Integer mucDa,
            String ghiChu,
            List<NguyenLieuThemRequest> nguyenLieuThem
    ) {}

    public record NguyenLieuThemRequest(
            UUID nguyenLieuId,
            Integer soLuong
    ) {}
}

