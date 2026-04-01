package TKPMHDT.Service.nguoidung;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * DangXuatService - UC03: Đăng xuất
 * Mục đích: Xóa session/token và đăng xuất người dùng
 */
@Service
public class DangXuatService {

    /**
     * Đăng xuất người dùng
     * Xóa SecurityContext
     */
    public void dangXuat() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Xóa phiên bản hiện tại
     * Alias cho dangXuat()
     */
    public void xoaPhoiBanHienTai() {
        dangXuat();
    }

    /**
     * Đăng xuất toàn bộ session của người dùng
     * Có thể mở rộng để xóa tất cả token trong cơ sở dữ liệu nếu sử dụng JWT
     */
    public void dangXuatToanBo() {
        SecurityContextHolder.clearContext();
        // TODO: Nếu sử dụng JWT, cần invalidate token trong database
        // tokenService.invalidateAllTokensForUser(userId);
    }
}
