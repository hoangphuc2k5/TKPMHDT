package TKPMHDT.Controller;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Service.FileStorageService;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/san-pham")
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final FileStorageService fileStorageService;

    public SanPhamController(SanPhamService sanPhamService, FileStorageService fileStorageService) {
        this.sanPhamService = sanPhamService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/nuoc-uong")
    public ResponseEntity<List<NuocUongSan>> layDanhSachNuocUong() {
        return ResponseEntity.ok(sanPhamService.layDanhSachNuocUong());
    }

    @GetMapping("/nuoc-uong/tim")
    public ResponseEntity<List<NuocUongSan>> timNuocUong(@RequestParam String ten) {
        return ResponseEntity.ok(sanPhamService.timNuocUongTheoTen(ten));
    }

    @GetMapping("/nuoc-uong/{id}")
    public ResponseEntity<NuocUongSan> layNuocUongTheoId(@PathVariable UUID id) {
        return sanPhamService.layNuocUongTheoId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
    @PostMapping("/nuoc-uong")
    public ResponseEntity<NuocUongSan> luuNuocUong(@RequestBody NuocUongSan nuocUongSan) {
        return ResponseEntity.ok(sanPhamService.luuNuocUong(nuocUongSan));
    }

    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
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

    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
    @PostMapping("/nguyen-lieu")
    public ResponseEntity<NguyenLieu> luuNguyenLieu(@RequestBody NguyenLieu nguyenLieu) {
        return ResponseEntity.ok(sanPhamService.luuNguyenLieu(nguyenLieu));
    }
}

