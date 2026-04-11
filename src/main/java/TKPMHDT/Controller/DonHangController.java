package TKPMHDT.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.enums.TrangThaiDonHangEnum;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Service.donhang.DonHangService;

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
        DonHang donHang = donHangService.taoDonHangTuGioHang(
                request.khachHangId(),
                request.diaChiId(),
                request.maGiamGiaCode(),
                request.phuongThuc()
        );
        return ResponseEntity.ok(donHang);
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @PostMapping("/tao-tu-san-pham")
    public ResponseEntity<DonHang> taoTuSanPham(@RequestBody TaoDonHangSanPhamRequest request) {
        DonHang donHang = donHangService.taoDonHangTuSanPham(
                request.khachHangId(),
                request.nuocUongId(),
                request.soLuong(),
                request.mucDuong(),
                request.mucDa(),
                request.ghiChu(),
                request.diaChiId(),
                request.maGiamGiaCode(),
                request.phuongThuc()
        );
        return ResponseEntity.ok(donHang);
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @GetMapping("/me")
    public ResponseEntity<List<DonHang>> layDonHangCuaToi(Principal principal) {
        return ResponseEntity.ok(donHangService.layDonHangCuaToi(principal.getName()));
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @GetMapping("/all")
    public ResponseEntity<List<DonHang>> layTatCaDonHang() {
        return ResponseEntity.ok(donHangService.layTatCaDonHang());
    }

    @PreAuthorize("hasAnyRole('KHACH_HANG','NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @GetMapping("/{donHangId}")
    public ResponseEntity<DonHang> layChiTietDonHang(@PathVariable UUID donHangId) {
        return donHangService.layTheoId(donHangId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PostMapping("/{donHangId}/xac-nhan")
    public ResponseEntity<DonHang> xacNhan(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.xacNhanDonHang(donHangId));
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PostMapping("/{donHangId}/giao-hang")
    public ResponseEntity<DonHang> giaoHang(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.giaoDonHang(donHangId));
    }

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PostMapping("/{donHangId}/hoan-thanh")
    public ResponseEntity<DonHang> hoanThanh(@PathVariable UUID donHangId) {
        return ResponseEntity.ok(donHangService.hoanThanhDonHang(donHangId));
    }

    @PreAuthorize("hasAnyRole('KHACH_HANG','NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
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
            Integer mucDuong,
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

    @PreAuthorize("hasAnyRole('NHAN_VIEN_BAN_HANG','QUAN_TRI_VIEN')")
    @PostMapping("/tao-tai-quay")
    public ResponseEntity<DonHang> taoTaiQuay(@RequestBody TaoDonHangTaiQuayRequest request) {
        // Convert ChiTietTaiQuayRequest list to Map list for service
        List<Map<String, Object>> chiTietMaps = request.chiTietItems().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nuocUongId", item.nuocUongId());
                    map.put("soLuong", item.soLuong());
                    map.put("duong", item.duong());
                    map.put("da", item.da());
                    map.put("thanhTien", item.thanhTien());
                    return map;
                })
                .collect(Collectors.toList());
        
        DonHang donHang = donHangService.taoDonHangTaiQuay(
                request.tenKhachHang(),
                request.soDienThoai(),
                chiTietMaps
        );
        return ResponseEntity.ok(donHang);
    }

    public record TaoDonHangTaiQuayRequest(
            String tenKhachHang,
            String soDienThoai,
            java.util.List<ChiTietTaiQuayRequest> chiTietItems
    ) {}

    public record ChiTietTaiQuayRequest(
            UUID nuocUongId,
            Integer soLuong,
            Integer duong,
            Integer da,
            java.math.BigDecimal thanhTien
    ) {}
}

