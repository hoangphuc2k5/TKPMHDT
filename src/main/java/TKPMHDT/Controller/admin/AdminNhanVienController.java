package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.hethong.NhatKyHeThong;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Repository.hethong.NhatKyHeThongRepository;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminNhanVienController {

    private final NguoiDungService nguoiDungService;
    private final NhatKyHeThongRepository nhatKyHeThongRepository;
    private final AdminPayloadHelper payloads;
    private final AdminAuditHelper audit;

    public AdminNhanVienController(
            NguoiDungService nguoiDungService,
            NhatKyHeThongRepository nhatKyHeThongRepository,
            AdminPayloadHelper payloads,
            AdminAuditHelper audit) {
        this.nguoiDungService = nguoiDungService;
        this.nhatKyHeThongRepository = nhatKyHeThongRepository;
        this.payloads = payloads;
        this.audit = audit;
    }

    @GetMapping("/nhan-vien")
    @PreAuthorize("hasAuthority('staff:manage')")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachNhanVien() {
        return ResponseEntity.ok(
                nguoiDungService.danhSachNhanVien().stream().map(payloads::toUserPayload).toList());
    }

    @PostMapping("/nhan-vien")
    @PreAuthorize("hasAnyAuthority('staff:manage','role:assign')")
    public ResponseEntity<Map<String, Object>> taoNhanVien(@RequestBody AdminApiDtos.TaoNhanVienRequest request) {
        VaiTro role = request.vaiTro() == null ? VaiTro.NHAN_VIEN_BAN_HANG : request.vaiTro();
        NguoiDung nv = nguoiDungService.taoNhanVien(
                request.tenDangNhap(), request.email(), request.matKhau(), role);
        audit.ghiLog("NHAN_VIEN", "TAO", nv.getTenDangNhap());
        return ResponseEntity.ok(payloads.toUserPayload(nv));
    }

    @PutMapping("/nhan-vien/{nhanVienId}")
    @PreAuthorize("hasAnyAuthority('staff:manage','role:assign')")
    public ResponseEntity<Map<String, Object>> capNhatNhanVien(
            @PathVariable UUID nhanVienId, @RequestBody AdminApiDtos.CapNhatNhanVienRequest request) {
        NguoiDung updated = nguoiDungService.capNhatNhanVien(
                nhanVienId, request.email(), request.vaiTro(), request.kichHoat());
        audit.ghiLog("NHAN_VIEN", "CAP_NHAT", updated.getId().toString());
        return ResponseEntity.ok(payloads.toUserPayload(updated));
    }

    @DeleteMapping("/nhan-vien/{nhanVienId}")
    @PreAuthorize("hasAuthority('staff:manage')")
    public ResponseEntity<Map<String, Object>> xoaNhanVien(@PathVariable UUID nhanVienId) {
        nguoiDungService.timTheoId(nhanVienId).ifPresent(user -> {
            if (audit.tenNguoiDangNhap().equalsIgnoreCase(user.getTenDangNhap())
                    && user.getVaiTro() == VaiTro.QUAN_TRI_VIEN) {
                throw new IllegalArgumentException("Không thể tự xóa tài khoản ADMIN của chính mình");
            }
        });
        nguoiDungService.xoaNguoiDung(nhanVienId);
        audit.ghiLog("NHAN_VIEN", "XOA", nhanVienId.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/nhan-vien/hoat-dong")
    @PreAuthorize("hasAuthority('staff:manage')")
    public ResponseEntity<List<NhatKyHeThong>> theoDoiHoatDongNhanVien() {
        List<NhatKyHeThong> logs = nhatKyHeThongRepository.findTop200ByOrderByThoiGianDesc().stream()
                .filter(l -> "NHAN_VIEN".equalsIgnoreCase(l.getMoDun())
                        || "DON_HANG".equalsIgnoreCase(l.getMoDun()))
                .toList();
        return ResponseEntity.ok(logs);
    }
}
