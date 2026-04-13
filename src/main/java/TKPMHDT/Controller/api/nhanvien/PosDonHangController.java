package TKPMHDT.Controller.api.nhanvien;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.DTO.ApiResponse;
import TKPMHDT.DTO.request.SanPhamOrderRequest;
import TKPMHDT.DTO.response.DonHangResponse;
import TKPMHDT.Service.donhang.DonHangService;
import TKPMHDT.Util.ResponseFactory;
import TKPMHDT.facade.PosFacade;
import lombok.RequiredArgsConstructor;

/**
 * API POS tại quầy (nhân viên bán hàng).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/don-hang/pos")
public class PosDonHangController {

    private final DonHangService donHangService;
    private final PosFacade posFacade;

    @PreAuthorize("hasAuthority('pos:create')")
    @GetMapping("/orders-offline")
    public ResponseEntity<ApiResponse<Page<DonHangResponse>>> layTatCaDonHangOffline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayDat").descending());
        Page<DonHangResponse> donHangs = donHangService.layTatCaDonHangOffline(pageable);
        return ResponseFactory.success(donHangs, "Lấy đơn hàng thành công");
    }

    @GetMapping("/{donHangId}")
    public ResponseEntity<ApiResponse<DonHangResponse>> layDonHangTaiQuay(@PathVariable UUID donHangId) {
        DonHangResponse donHang = donHangService.layDonHangTaiQuay(donHangId);
        return ResponseFactory.success(donHang, "Lấy đơn hàng tại quầy thành công");
    }

    @PreAuthorize("hasAuthority('pos:create')")
    @PostMapping("/tao-don-tai-quay")
    public ResponseEntity<ApiResponse<UUID>> taoDonTaiQuay() {
        UUID donHangId = posFacade.taoDonHangTaiQuay();
        return ResponseFactory.success(donHangId, "Tạo đơn thành công");
    }

    @PreAuthorize("hasAuthority('pos:create')")
    @PostMapping("/{donHangId}/them-san-pham")
    public ResponseEntity<ApiResponse<DonHangResponse>> themSanPham(
            @PathVariable UUID donHangId,
            @RequestBody SanPhamOrderRequest request) {
        DonHangResponse donHang = posFacade.themSanPham(donHangId, request);
        return ResponseFactory.success(donHang, "Thêm sản phẩm thành công");
    }

    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/{donHangId}/ma-giam-gia")
    public ResponseEntity<ApiResponse<DonHangResponse>> apDungMaGiamGiaPos(
            @PathVariable UUID donHangId,
            @RequestBody(required = false) Map<String, String> body) {
        String raw = body != null ? body.get("ma") : null;
        String ma = raw != null ? raw.trim() : "";
        DonHangResponse dh = posFacade.apDungMaGiamGiaDonTaiQuay(donHangId, ma.isEmpty() ? null : ma);
        String msg = ma.isEmpty() ? "Đã gỡ mã giảm giá" : "Áp dụng mã giảm giá thành công";
        return ResponseFactory.success(dh, msg);
    }

    @PreAuthorize("hasAuthority('order:update')")
    @PatchMapping("/{donHangId}/xac-nhan")
    public ResponseEntity<ApiResponse<UUID>> xacNhanDonHang(@PathVariable UUID donHangId) {
        posFacade.xacNhanDonHangVaTruKho(donHangId);
        return ResponseFactory.success(donHangId, "Xác nhận đơn hàng thành công");
    }

    @PreAuthorize("hasAuthority('order:update')")
    @PatchMapping("/{donHangId}/hoan-thanh")
    public ResponseEntity<ApiResponse<UUID>> hoanThanhDonHang(@PathVariable UUID donHangId) {
        posFacade.hoanThanhDonHang(donHangId);
        return ResponseFactory.success(donHangId, "Hoàn thành đơn hàng thành công");
    }

    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/chi-tiet/{chiTietDonHangId}/xoa")
    public ResponseEntity<ApiResponse<UUID>> xoaChiTietDonHang(@PathVariable UUID chiTietDonHangId) {
        posFacade.xoaChiTietDonHang(chiTietDonHangId);
        return ResponseFactory.success(chiTietDonHangId, "Xóa chi tiết đơn hàng thành công");
    }

    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/chi-tiet/{chiTietDonHangId}/tang")
    public ResponseEntity<ApiResponse<String>> tangSoLuong(@PathVariable UUID chiTietDonHangId) {
        posFacade.tangSoLuong(chiTietDonHangId);
        return ResponseFactory.success(null, "Tăng số lượng thành công");
    }

    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/chi-tiet/{chiTietDonHangId}/giam")
    public ResponseEntity<ApiResponse<String>> giamSoLuong(@PathVariable UUID chiTietDonHangId) {
        posFacade.giamSoLuong(chiTietDonHangId);
        return ResponseFactory.success(null, "Giảm số lượng thành công");
    }
}
