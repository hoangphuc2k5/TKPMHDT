package TKPMHDT.Controller.api.khachhang;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import TKPMHDT.DTO.ApiResponse;
import TKPMHDT.DTO.response.XemDonHangResponse;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Service.donhang.DonHangRealtimeService;
import TKPMHDT.Service.donhang.DonHangService;
import TKPMHDT.Util.ResponseFactory;
import lombok.RequiredArgsConstructor;

/**
 * API đơn hàng phía khách hàng (đặt hàng, theo dõi, hủy).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/don-hang")
public class DonHangKhachHangController {

    private final DonHangService donHangService;
    private final DonHangRealtimeService donHangRealtimeService;

    @PreAuthorize("hasAuthority('order:customer-create')")
    @PostMapping("/tao-tu-gio-hang")
    public ResponseEntity<DonHang> taoTuGioHang(@RequestBody TaoDonHangRequest request) {
        DonHang donHang = donHangService.taoDonHangTuGioHang(
                request.khachHangId(),
                request.diaChiId(),
                request.maGiamGiaCode(),
                request.phuongThuc());
        return ResponseEntity.ok(donHang);
    }

    @PreAuthorize("hasAuthority('order:customer-create')")
    @PostMapping("/tao-tu-san-pham")
    public ResponseEntity<DonHang> taoTuSanPham(@RequestBody TaoDonHangSanPhamRequest request) {
        DonHang donHang = donHangService.taoDonHangTuSanPham(
                request.khachHangId(),
                request.nuocUongId(),
                request.soLuong(),
                request.mucDa(),
                request.ghiChu(),
                request.diaChiId(),
                request.maGiamGiaCode(),
                request.phuongThuc());
        return ResponseEntity.ok(donHang);
    }

    @PreAuthorize("hasAuthority('order:track')")
    @GetMapping("/me")
    public ResponseEntity<List<DonHang>> layDonHangCuaToi(Principal principal) {
        return ResponseEntity.ok(donHangService.layDonHangCuaToi(principal.getName()));
    }

    @GetMapping("/{donHangId}")
    public ResponseEntity<ApiResponse<XemDonHangResponse>> layChiTietDonHang(@PathVariable UUID donHangId) {
        XemDonHangResponse response = donHangService.layChiTietDonHang(donHangId);
        return ResponseFactory.success(response, "Lấy chi tiết đơn hàng thành công");
    }

    @PreAuthorize("hasAnyAuthority('order:track','order:view')")
    @GetMapping(value = "/{donHangId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter theoDoiDonHangRealtime(@PathVariable UUID donHangId) {
        return donHangRealtimeService.subscribe(donHangId);
    }

    @PreAuthorize("hasAnyAuthority('order:track','order:update')")
    @PostMapping("/{donHangId}/huy")
    public ResponseEntity<DonHang> huy(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.huyDonHang(donHangId));
    }

    public record TaoDonHangRequest(UUID khachHangId, UUID diaChiId, String maGiamGiaCode, PhuongThucThanhToanEnum phuongThuc) {
    }

    public record TaoDonHangSanPhamRequest(
            UUID khachHangId,
            UUID nuocUongId,
            Integer soLuong,
            Integer mucDa,
            String ghiChu,
            UUID diaChiId,
            String maGiamGiaCode,
            PhuongThucThanhToanEnum phuongThuc) {
    }
}
