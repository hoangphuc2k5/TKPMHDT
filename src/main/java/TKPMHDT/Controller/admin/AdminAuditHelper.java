package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.hethong.NhatKyHeThong;
import TKPMHDT.Repository.hethong.NhatKyHeThongRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AdminAuditHelper {

    private final NhatKyHeThongRepository nhatKyHeThongRepository;

    public AdminAuditHelper(NhatKyHeThongRepository nhatKyHeThongRepository) {
        this.nhatKyHeThongRepository = nhatKyHeThongRepository;
    }

    public void ghiLog(String module, String action, String detail) {
        NhatKyHeThong log = NhatKyHeThong.builder()
                .moDun(module)
                .hanhDong(action)
                .chiTiet(detail)
                .nguoiThucHien(tenNguoiDangNhap())
                .build();
        nhatKyHeThongRepository.save(log);
    }

    public String tenNguoiDangNhap() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }
}
