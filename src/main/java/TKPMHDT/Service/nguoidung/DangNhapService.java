package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DangNhapService - UC02: Đăng nhập
 * Mục đích: Xác thực người dùng và cấp phát session/token
 */
@Service
public class DangNhapService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public DangNhapService(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Xác thực người dùng bằng tên đăng nhập và mật khẩu
     * @param tenDangNhap - Tên đăng nhập
     * @param matKhau - Mật khẩu plaintext
     * @return Đối tượng NguoiDung nếu xác thực thành công
     * @throws IllegalArgumentException nếu tên đăng nhập không tồn tại hoặc mật khẩu sai
     */
    @Transactional(readOnly = true)
    public NguoiDung xacThucDangNhap(String tenDangNhap, String matKhau) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(matKhau, nguoiDung.getMatKhauHash())) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        return nguoiDung;
    }

    /**
     * Xác thực người dùng bằng email và mật khẩu
     * @param email - Email
     * @param matKhau - Mật khẩu plaintext
     * @return Đối tượng NguoiDung nếu xác thực thành công
     */
    @Transactional(readOnly = true)
    public NguoiDung xacThucDangNhapByEmail(String email, String matKhau) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(matKhau, nguoiDung.getMatKhauHash())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }

        return nguoiDung;
    }
}
