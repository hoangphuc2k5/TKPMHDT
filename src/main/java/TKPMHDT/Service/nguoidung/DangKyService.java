package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.PasswordHistory;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Repository.nguoidung.PasswordHistoryRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.security.PasswordPolicyValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DangKyService - UC01: Đăng ký tài khoản
 * Mục đích: Cấp xác minh và lưu trữ tài khoản người dùng mới
 */
@Service
public class DangKyService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;

    public DangKyService(
            NguoiDungRepository nguoiDungRepository,
            PasswordHistoryRepository passwordHistoryRepository,
            PasswordEncoder passwordEncoder,
            PasswordPolicyValidator passwordPolicyValidator) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    /**
     * Đăng ký tài khoản khách hàng
     * @param tenDangNhap - Tên đăng nhập (unique)
     * @param email - Email (unique)
     * @param matKhau - Mật khẩu plaintext
     * @param hoTen - Họ tên khách hàng
     * @return Đối tượng NguoiDung đã lưu
     * @throws IllegalArgumentException nếu tên đăng nhập hoặc email đã tồn tại
     */
    @Transactional
    public NguoiDung dangKyKhachHang(String tenDangNhap, String email, String matKhau, String hoTen) {
        // Kiểm tra tên đăng nhập đã tồn tại
        if (nguoiDungRepository.findByTenDangNhap(tenDangNhap).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng");
        }

        // Kiểm tra email đã tồn tại
        if (nguoiDungRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email đã được đăng ký");
        }

        passwordPolicyValidator.validateOrThrow(matKhau);

        // Hash mật khẩu
        String matKhauHash = passwordEncoder.encode(matKhau);

        // Tạo KhachHang (subclass của NguoiDung)
        KhachHang khachHang = KhachHang.builder()
                .tenDangNhap(tenDangNhap)
                .email(email)
                .matKhauHash(matKhauHash)
                .vaiTro(VaiTro.KHACH_HANG)
                .hoTen(hoTen)
                .trangThaiHoatDong(true)
                .build();

        NguoiDung saved = nguoiDungRepository.save(khachHang);
        passwordHistoryRepository.save(PasswordHistory.builder()
                .nguoiDung(saved)
                .matKhauHash(saved.getMatKhauHash())
                .build());
        return saved;
    }

    /**
     * Kiểm tra định dạng email
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
