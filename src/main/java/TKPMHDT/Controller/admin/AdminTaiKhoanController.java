package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminTaiKhoanController {

    private final NguoiDungService nguoiDungService;
    private final AdminPayloadHelper payloads;
    private final AdminAuditHelper audit;

    public AdminTaiKhoanController(
            NguoiDungService nguoiDungService, AdminPayloadHelper payloads, AdminAuditHelper audit) {
        this.nguoiDungService = nguoiDungService;
        this.payloads = payloads;
        this.audit = audit;
    }

    @PatchMapping("/tai-khoan/trang-thai")
    public ResponseEntity<Map<String, Object>> capNhatTrangThaiTaiKhoan(
            @RequestBody AdminApiDtos.CapNhatTrangThaiTaiKhoanRequest request) {
        if (request.nguoiDungId() == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "nguoiDungId là bắt buộc"));
        }
        NguoiDung updated =
                nguoiDungService.khoaMoTaiKhoan(request.nguoiDungId(), request.active() != null && request.active());
        audit.ghiLog("TAI_KHOAN", "TRANG_THAI", updated.getId() + ":" + updated.isTrangThaiHoatDong());
        return ResponseEntity.ok(Map.of("success", true, "user", payloads.toUserPayload(updated)));
    }
}
