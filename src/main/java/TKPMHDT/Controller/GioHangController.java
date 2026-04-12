package TKPMHDT.Controller;

import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Service.giohang.GioHangService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.DTO.request.ToppingRequest;

@RestController
@RequestMapping("/api/gio-hang")
@PreAuthorize("hasAuthority('cart:manage')")
public class GioHangController {

    private final GioHangService gioHangService;

    public GioHangController(GioHangService gioHangService) {
        this.gioHangService = gioHangService;
    }

    @GetMapping("/{khachHangId}")
    public ResponseEntity<GioHang> layHoacTaoGioHang(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(gioHangService.layVaDongBoGiaTheoKhuyenMai(khachHangId));
    }

    @PostMapping("/them-mat-hang")
    public ResponseEntity<GioHang> themMatHang(@RequestBody ThemMatHangRequest request) {
        GioHang gioHang = gioHangService.themMatHang(
                request.khachHangId(),
                request.nuocUongId(),
                request.soLuong(),
                request.mucDa(),
                request.ghiChu(),
                request.toppings()
        );
        return ResponseEntity.ok(gioHang);
    }

    @PatchMapping("/{khachHangId}/mat-hang/{chiTietGioHangId}")
    public ResponseEntity<GioHang> capNhatMatHang(
            @PathVariable UUID khachHangId,
            @PathVariable UUID chiTietGioHangId,
            @RequestBody CapNhatMatHangRequest request
    ) {
        return ResponseEntity.ok(gioHangService.capNhatMatHang(
                khachHangId,
                chiTietGioHangId,
                request.soLuong(),
                request.mucDa(),
                request.ghiChu(),
                request.toppings()
        ));
    }

    @DeleteMapping("/{khachHangId}/mat-hang/{chiTietGioHangId}")
    public ResponseEntity<GioHang> xoaMatHang(@PathVariable UUID khachHangId, @PathVariable UUID chiTietGioHangId) {
        return ResponseEntity.ok(gioHangService.xoaMatHang(khachHangId, chiTietGioHangId));
    }

    @DeleteMapping("/{khachHangId}/xoa-het")
    public ResponseEntity<GioHang> xoaHet(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(gioHangService.xoaHet(khachHangId));
    }

    @PatchMapping("/{khachHangId}/mat-hang/{chiTietGioHangId}/chon")
    public ResponseEntity<GioHang> capNhatChonMatHang(
            @PathVariable UUID khachHangId,
            @PathVariable UUID chiTietGioHangId,
            @RequestBody CapNhatChonMatHangRequest request
    ) {
        return ResponseEntity.ok(gioHangService.capNhatChonMatHang(khachHangId, chiTietGioHangId, request.duocChon()));
    }

    @PatchMapping("/{khachHangId}/chon-tat-ca")
    public ResponseEntity<GioHang> capNhatChonTatCa(
            @PathVariable UUID khachHangId,
            @RequestBody CapNhatChonTatCaRequest request
    ) {
        return ResponseEntity.ok(gioHangService.capNhatChonTatCa(khachHangId, request.duocChon()));
    }

    public record ThemMatHangRequest(
            UUID khachHangId,
            UUID nuocUongId,
            int soLuong,
            Integer mucDa,
            String ghiChu,
            List<ToppingRequest> toppings
    ) {
    }

    public record CapNhatMatHangRequest(
            int soLuong,
            Integer mucDa,
            String ghiChu,
            List<ToppingRequest> toppings
    ) {
    }

    public record CapNhatChonMatHangRequest(boolean duocChon) {
    }

    public record CapNhatChonTatCaRequest(boolean duocChon) {
    }
}

