package TKPMHDT.Controller.api.nhanvien;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.DTO.ApiResponse;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Service.donhang.DonHangService;
import TKPMHDT.Util.ResponseFactory;
import TKPMHDT.facade.PosFacade;
import lombok.RequiredArgsConstructor;

/**
 * API đơn hàng cho nhân viên / xử lý đơn (danh sách, cập nhật trạng thái, POS tại quầy).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/don-hang")
public class DonHangNhanVienController {

    private final DonHangService donHangService;
    private final PosFacade posFacade;

    @GetMapping("/all")
    public ResponseEntity<List<DonHang>> layTatCaDonHang() {
        return ResponseEntity.ok(donHangService.layTatCaDonHang());
    }

    @PreAuthorize("hasAuthority('order:update')")
    @PostMapping("/{donHangId}/xac-nhan")
    public ResponseEntity<DonHang> xacNhan(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.xacNhanDonHang(donHangId));
    }

    @PreAuthorize("hasAuthority('order:update')")
    @PostMapping("/{donHangId}/giao-hang")
    public ResponseEntity<DonHang> giaoHang(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.giaoDonHang(donHangId));
    }

    @PreAuthorize("hasAuthority('order:update')")
    @PostMapping("/{donHangId}/hoan-thanh")
    public ResponseEntity<DonHang> hoanThanh(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.hoanThanhDonHang(donHangId));
    }

    @PreAuthorize("hasAuthority('order:update')")
    @PatchMapping("/{donHangId}/hoan-thanh-online")
    public ResponseEntity<ApiResponse<Object>> hoanThanhDonHangOnline(@PathVariable UUID donHangId) {
        posFacade.hoanThanhDonHangOnline(donHangId);
        return ResponseFactory.success(null, "Hoàn thành đơn hàng online thành công");
    }
}
