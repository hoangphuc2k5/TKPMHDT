package TKPMHDT.Controller;

import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;
import TKPMHDT.Service.thanhtoan.ThanhToanService;
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
@RequestMapping("/api/thanh-toan")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;

    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }

    @PreAuthorize("hasAnyRole('KHACH_HANG','NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PostMapping
    public ResponseEntity<ThanhToan> taoThanhToan(@RequestBody TaoThanhToanRequest request) {
        ThanhToan thanhToan = thanhToanService.taoThanhToan(request.donHangId(), request.phuongThuc());
        return ResponseEntity.ok(thanhToan);
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PatchMapping("/{thanhToanId}/trang-thai")
    public ResponseEntity<ThanhToan> capNhatTrangThai(
            @PathVariable UUID thanhToanId,
            @RequestBody CapNhatTrangThaiThanhToanRequest request
    ) {
        ThanhToan thanhToan = thanhToanService.capNhatTrangThai(thanhToanId, request.trangThaiMoi());
        return ResponseEntity.ok(thanhToan);
    }

    @PreAuthorize("hasAnyRole('KHACH_HANG','NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<ThanhToan> layTheoDonHang(@PathVariable UUID donHangId) {
        return thanhToanService.layTheoDonHangId(donHangId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record TaoThanhToanRequest(UUID donHangId, PhuongThucThanhToanEnum phuongThuc) {
    }

    public record CapNhatTrangThaiThanhToanRequest(TrangThaiThanhToanEnum trangThaiMoi) {
    }
}

