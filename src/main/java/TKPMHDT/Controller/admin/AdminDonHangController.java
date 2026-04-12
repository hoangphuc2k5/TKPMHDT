package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiChoXacNhan;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaGiao;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaHuy;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaXacNhan;
import TKPMHDT.Repository.donhang.DonHangRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminDonHangController {

    private final DonHangRepository donHangRepository;
    private final AdminPayloadHelper payloads;
    private final AdminAuditHelper audit;

    public AdminDonHangController(
            DonHangRepository donHangRepository, AdminPayloadHelper payloads, AdminAuditHelper audit) {
        this.donHangRepository = donHangRepository;
        this.payloads = payloads;
        this.audit = audit;
    }

    @GetMapping("/don-hang")
    public ResponseEntity<List<DonHang>> layDanhSachDonHang(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        List<DonHang> all = donHangRepository.findAll();
        List<DonHang> filtered = all.stream()
                .filter(o -> {
                    if (tuNgay == null || denNgay == null || o.getNgayDat() == null) {
                        return true;
                    }
                    LocalDate d = o.getNgayDat().toLocalDate();
                    return !d.isBefore(tuNgay) && !d.isAfter(denNgay);
                })
                .filter(o -> trangThai == null
                        || trangThai.isBlank()
                        || payloads.mapTrangThaiVeDb(trangThai).equalsIgnoreCase(o.getTrangThaiDb()))
                .filter(o -> {
                    if (q == null || q.isBlank()) {
                        return true;
                    }
                    String k = q.toLowerCase(Locale.ROOT);
                    String idText = o.getId() != null ? o.getId().toString().toLowerCase(Locale.ROOT) : "";
                    String ten = o.getKhachHang() != null && o.getKhachHang().getTenDangNhap() != null
                            ? o.getKhachHang().getTenDangNhap().toLowerCase(Locale.ROOT)
                            : "";
                    return idText.contains(k) || ten.contains(k);
                })
                .sorted(Comparator.comparing(DonHang::getNgayDat, Comparator.nullsLast(LocalDateTime::compareTo))
                        .reversed())
                .toList();
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<Map<String, Object>> layChiTietDonHang(@PathVariable UUID donHangId) {
        return donHangRepository
                .findById(donHangId)
                .map(donHang -> ResponseEntity.ok(payloads.toDonHangDetailPayload(donHang)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/don-hang/{donHangId}/trang-thai")
    public ResponseEntity<DonHang> capNhatTrangThaiDonHang(
            @PathVariable UUID donHangId, @RequestBody AdminApiDtos.CapNhatTrangThaiDonHangRequest request) {
        return donHangRepository
                .findById(donHangId)
                .map(donHang -> {
                    String dbStatus = payloads.mapTrangThaiVeDb(request.trangThai());
                    if ("CHO_XAC_NHAN".equals(dbStatus)) {
                        donHang.setTrangThai(new TrangThaiChoXacNhan());
                    } else if ("DA_XAC_NHAN".equals(dbStatus)) {
                        donHang.setTrangThai(new TrangThaiDaXacNhan());
                    } else if ("DA_GIAO".equals(dbStatus)) {
                        donHang.setTrangThai(new TrangThaiDaGiao());
                    } else if ("DA_HUY".equals(dbStatus)) {
                        donHang.setTrangThai(new TrangThaiDaHuy());
                    }
                    donHang.setTrangThaiDb(dbStatus);
                    DonHang updated = donHangRepository.save(donHang);
                    audit.ghiLog("DON_HANG", "CAP_NHAT_TRANG_THAI", updated.getId() + ":" + dbStatus);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
