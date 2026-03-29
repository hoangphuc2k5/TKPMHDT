package TKPMHDT.Controller;

import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Service.FileStorageService;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import java.util.Map;
import java.util.UUID;
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
@RequestMapping("/api/nguoi-dung")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;
    private final FileStorageService fileStorageService;

    public NguoiDungController(NguoiDungService nguoiDungService, FileStorageService fileStorageService) {
        this.nguoiDungService = nguoiDungService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/dang-ky-khach-hang")
    public ResponseEntity<KhachHang> dangKyKhachHang(@RequestBody DangKyKhachHangRequest request) {
        KhachHang khachHang = nguoiDungService.dangKyKhachHang(
                request.tenDangNhap(),
                request.email(),
                request.matKhauHash()
        );
        return ResponseEntity.ok(khachHang);
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<NguoiDung> uploadAvatar(
            @PathVariable UUID id,
            @RequestParam("avatar") MultipartFile file) {
        return nguoiDungService.timTheoId(id).map(nguoiDung -> {
            String url = fileStorageService.storeFile(file);
            nguoiDung.setAvatar(url);
            return ResponseEntity.ok(nguoiDungService.luuNguoiDung(nguoiDung));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDung> layTheoId(@PathVariable UUID id) {
        return nguoiDungService.timTheoId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ten-dang-nhap/{tenDangNhap}")
    public ResponseEntity<NguoiDung> layTheoTenDangNhap(@PathVariable String tenDangNhap) {
        return nguoiDungService.timTheoTenDangNhap(tenDangNhap)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/tim-theo-email")
    public ResponseEntity<?> layTheoEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        return nguoiDungService.timTheoEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record DangKyKhachHangRequest(
            String tenDangNhap,
            String email,
            String matKhauHash
    ) {
    }
}

