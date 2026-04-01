package TKPMHDT.Controller;

import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Service.giohang.GioHangService;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gio-hang")
public class GioHangController {

    private final GioHangService gioHangService;

    public GioHangController(GioHangService gioHangService) {
        this.gioHangService = gioHangService;
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @GetMapping("/{khachHangId}")
    public ResponseEntity<GioHang> layHoacTaoGioHang(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(gioHangService.layHoacTaoGioHang(khachHangId));
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @PostMapping("/them-mat-hang")
    public ResponseEntity<GioHang> themMatHang(@RequestBody ThemMatHangRequest request) {
        GioHang gioHang = gioHangService.themMatHang(
                request.khachHangId(),
                request.nuocUongId(),
                request.soLuong(),
                request.mucDuong(),
                request.mucDa(),
                request.ghiChu()
        );
        return ResponseEntity.ok(gioHang);
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @DeleteMapping("/{khachHangId}/mat-hang/{chiTietGioHangId}")
    public ResponseEntity<GioHang> xoaMatHang(@PathVariable UUID khachHangId, @PathVariable UUID chiTietGioHangId) {
        return ResponseEntity.ok(gioHangService.xoaMatHang(khachHangId, chiTietGioHangId));
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @DeleteMapping("/{khachHangId}/xoa-het")
    public ResponseEntity<GioHang> xoaHet(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(gioHangService.xoaHet(khachHangId));
    }

    public record ThemMatHangRequest(
            UUID khachHangId,
            UUID nuocUongId,
            int soLuong,
            Integer mucDuong,
            Integer mucDa,
            String ghiChu
    ) {
    }
}

