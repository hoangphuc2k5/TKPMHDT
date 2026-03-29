package TKPMHDT.Controller;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.enums.TrangThaiDonHangEnum;
import TKPMHDT.Service.donhang.DonHangService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/don-hang")
public class DonHangController {

    private final DonHangService donHangService;

    public DonHangController(DonHangService donHangService) {
        this.donHangService = donHangService;
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @PostMapping("/tao-tu-gio-hang")
    public ResponseEntity<DonHang> taoTuGioHang(@RequestBody TaoDonHangRequest request) {
        DonHang donHang = donHangService.taoDonHangTuGioHang(request.khachHangId(), request.maGiamGiaCode());
        return ResponseEntity.ok(donHang);
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PatchMapping("/{donHangId}/trang-thai")
    public ResponseEntity<DonHang> capNhatTrangThai(
            @PathVariable UUID donHangId,
            @RequestBody CapNhatTrangThaiRequest request
    ) {
        DonHang donHang = donHangService.capNhatTrangThai(donHangId, request.trangThaiMoi());
        return ResponseEntity.ok(donHang);
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @GetMapping("/khach-hang/{khachHangId}")
    public ResponseEntity<List<DonHang>> layTheoKhachHang(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(donHangService.layDonHangTheoKhachHang(khachHangId));
    }

    @PreAuthorize("hasAnyRole('KHACH_HANG','NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @GetMapping("/{donHangId}")
    public ResponseEntity<DonHang> layTheoId(@PathVariable UUID donHangId) {
        return donHangService.layTheoId(donHangId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @GetMapping("/don-hang-cua-toi")
    public ResponseEntity<List<DonHang>> donHangCuaToi(Principal principal) {
        return ResponseEntity.ok(donHangService.layDonHangCuaToi(principal.getName()));
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PostMapping("/tai-quay")
    public ResponseEntity<DonHang> taoDonTaiQuay(@RequestBody DonTaiQuayRequest request) {
        return ResponseEntity.ok(
                donHangService.xuLyDonTaiQuay(request.khachHangId(), request.nuocUongId(), request.soLuong())
        );
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @GetMapping("/{donHangId}/in-hoa-don")
    public ResponseEntity<String> inHoaDon(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.inHoaDon(donHangId));
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @GetMapping("/{donHangId}/in-phieu-giao")
    public ResponseEntity<String> inPhieuGiao(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.inPhieuGiao(donHangId));
    }

    public record TaoDonHangRequest(UUID khachHangId, String maGiamGiaCode) {
    }

    public record CapNhatTrangThaiRequest(TrangThaiDonHangEnum trangThaiMoi) {
    }

    public record DonTaiQuayRequest(UUID khachHangId, UUID nuocUongId, int soLuong) {
    }
}

