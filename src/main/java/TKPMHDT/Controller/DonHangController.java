package TKPMHDT.Controller;

import java.security.Principal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.enums.TrangThaiDonHangEnum;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Service.donhang.DonHangService;
import TKPMHDT.Service.donhang.DonHangRealtimeService;
import TKPMHDT.Util.ResponseFactory;
import TKPMHDT.facade.PosFacade;
import lombok.RequiredArgsConstructor;
import TKPMHDT.DTO.ApiResponse;
import TKPMHDT.DTO.request.SanPhamOrderRequest;
import TKPMHDT.DTO.response.DonHangResponse;
import TKPMHDT.DTO.response.XemDonHangResponse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/don-hang")
public class DonHangController {

    private final DonHangService donHangService;
    private final DonHangRealtimeService donHangRealtimeService;
    private final PosFacade posFacade;
    

    @PreAuthorize("hasAuthority('order:customer-create')")
    @PostMapping("/tao-tu-gio-hang")
    public ResponseEntity<DonHang> taoTuGioHang(@RequestBody TaoDonHangRequest request) {
        DonHang donHang = donHangService.taoDonHangTuGioHang(
                request.khachHangId(),
                request.diaChiId(),
                request.maGiamGiaCode(),
                request.phuongThuc()
        );
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
                request.phuongThuc()
        );
        return ResponseEntity.ok(donHang);
    }

    @PreAuthorize("hasAuthority('order:track')")
    @GetMapping("/me")
    public ResponseEntity<List<DonHang>> layDonHangCuaToi(Principal principal) {
        return ResponseEntity.ok(donHangService.layDonHangCuaToi(principal.getName()));
    }

    //@PreAuthorize("hasAuthority('order:view')")
    @GetMapping("/all")
    public ResponseEntity<List<DonHang>> layTatCaDonHang() {
        return ResponseEntity.ok(donHangService.layTatCaDonHang());
    }

    //@PreAuthorize("hasAnyAuthority('order:track','order:view')")
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

    @PreAuthorize("hasAnyAuthority('order:track','order:update')")
    @PostMapping("/{donHangId}/huy")
    public ResponseEntity<DonHang> huy(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.huyDonHang(donHangId));
    }

    public record TaoDonHangRequest(UUID khachHangId, UUID diaChiId, String maGiamGiaCode, TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum phuongThuc) {
    }

    public record TaoDonHangSanPhamRequest(
            UUID khachHangId,
            UUID nuocUongId,
            Integer soLuong,
            Integer mucDa,
            String ghiChu,
            UUID diaChiId,
            String maGiamGiaCode,
            TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum phuongThuc
    ) {
    }

    public record CapNhatTrangThaiRequest(TrangThaiDonHangEnum trangThaiMoi) {
    }

    public record DonTaiQuayRequest(UUID khachHangId, UUID nuocUongId, int soLuong) {
    }


    // POS API
    // Xem tất cả đơn hàng tại quầy
    @PreAuthorize("hasAuthority('pos:create')")
    @GetMapping("/pos/orders-offline")
    public ResponseEntity<ApiResponse<Page<DonHangResponse>>> layTatCaDonHangOffline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayDat").descending());

        Page<DonHangResponse> donHangs = donHangService.layTatCaDonHangOffline(pageable);

        return ResponseFactory.success(donHangs, "Lấy đơn hàng thành công");
    }

    // Xem đơn hàng tại quầy
    //@PreAuthorize("hasAuthority('pos:create')")
    @GetMapping("/pos/{donHangId}")
    public ResponseEntity<ApiResponse<DonHangResponse>> layDonHangTaiQuay(@PathVariable UUID donHangId) {
        DonHangResponse donHang = donHangService.layDonHangTaiQuay(donHangId);
        return ResponseFactory.success(donHang, "Lấy đơn hàng tại quầy thành công");
    }

    // Tạo đơn hàng tại quầy
    @PreAuthorize("hasAuthority('pos:create')")
    @PostMapping("/pos/tao-don-tai-quay")
    public ResponseEntity<ApiResponse<UUID>> taoDonTaiQuay() {

        UUID donHangId = posFacade.taoDonHangTaiQuay();

        return ResponseFactory.success(donHangId, "Tạo đơn thành công");
    }

    // Thêm sản phẩm vào đơn hàng tại quầy
    @PreAuthorize("hasAuthority('pos:create')")
    @PostMapping("/pos/{donHangId}/them-san-pham")
    public ResponseEntity<ApiResponse<DonHangResponse>> themSanPham(
            @PathVariable UUID donHangId,
            @RequestBody SanPhamOrderRequest request
    ) {

        DonHangResponse donHang = posFacade.themSanPham(donHangId, request);

        return ResponseFactory.success(donHang, "Thêm sản phẩm thành công");
    }

    /** Áp dụng hoặc gỡ mã voucher (body {@code { "ma": "CODE" }} hoặc {@code ma} rỗng để gỡ). */
    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/pos/{donHangId}/ma-giam-gia")
    public ResponseEntity<ApiResponse<DonHangResponse>> apDungMaGiamGiaPos(
            @PathVariable UUID donHangId,
            @RequestBody(required = false) Map<String, String> body) {
        String raw = body != null ? body.get("ma") : null;
        String ma = raw != null ? raw.trim() : "";
        DonHangResponse dh = posFacade.apDungMaGiamGiaDonTaiQuay(donHangId, ma.isEmpty() ? null : ma);
        String msg = ma.isEmpty() ? "Đã gỡ mã giảm giá" : "Áp dụng mã giảm giá thành công";
        return ResponseFactory.success(dh, msg);
    }

    // Xác nhận đơn hàng tại quầy
    @PreAuthorize("hasAuthority('order:update')")
    @PatchMapping("/pos/{donHangId}/xac-nhan")
    public ResponseEntity<ApiResponse<UUID>> xacNhanDonHang(@PathVariable UUID donHangId) {
        posFacade.xacNhanDonHangVaTruKho(donHangId);
        return ResponseFactory.success(donHangId, "Xác nhận đơn hàng thành công");
    }

    //Hoàn thành đơn hàng tại quầy
    @PreAuthorize("hasAuthority('order:update')")
    @PatchMapping("/pos/{donHangId}/hoan-thanh")
    public ResponseEntity<ApiResponse<UUID>> hoanThanhDonHang(@PathVariable UUID donHangId) {
        posFacade.hoanThanhDonHang(donHangId);
        return ResponseFactory.success(donHangId, "Hoàn thành đơn hàng thành công");
    }

    // Bỏ sản phẩm khỏi đơn hàng tại quầy
    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/pos/chi-tiet/{chiTietDonHangId}/xoa")
    public ResponseEntity<ApiResponse<UUID>> xoaChiTietDonHang(@PathVariable UUID chiTietDonHangId) {
        posFacade.xoaChiTietDonHang(chiTietDonHangId);
        return ResponseFactory.success(chiTietDonHangId, "Xóa chi tiết đơn hàng thành công");
    }

    // Tăng số lượng sản phẩm trong đơn hàng tại quầy
    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/pos/chi-tiet/{chiTietDonHangId}/tang")
    public ResponseEntity<ApiResponse<String>> tangSoLuong(@PathVariable UUID chiTietDonHangId) {
        posFacade.tangSoLuong(chiTietDonHangId);
        return ResponseFactory.success(null,"Tăng số lượng thành công");
    }

    // Giảm số lượng sản phẩm trong đơn hàng tại quầy
    @PreAuthorize("hasAuthority('pos:create')")
    @PatchMapping("/pos/chi-tiet/{chiTietDonHangId}/giam")
    public ResponseEntity<ApiResponse<String>> giamSoLuong(@PathVariable UUID chiTietDonHangId) {
        posFacade.giamSoLuong(chiTietDonHangId);
        return ResponseFactory.success(null,"Giảm số lượng thành công");
    }

    
}

