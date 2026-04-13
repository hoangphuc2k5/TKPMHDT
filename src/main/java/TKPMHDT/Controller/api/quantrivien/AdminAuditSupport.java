package TKPMHDT.Controller.api.quantrivien;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import TKPMHDT.Entity.hethong.NhatKyHeThong;
import TKPMHDT.Repository.hethong.NhatKyHeThongRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminAuditSupport {

    private final NhatKyHeThongRepository nhatKyHeThongRepository;

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
