package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NhanVienBanHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.nguoidung.NhanVienBanHangRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienBanHangRepository nhanVienBanHangRepository;
    private final PasswordEncoder passwordEncoder;

    public NguoiDungService(
            NguoiDungRepository nguoiDungRepository,
            KhachHangRepository khachHangRepository,
            NhanVienBanHangRepository nhanVienBanHangRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienBanHangRepository = nhanVienBanHangRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public KhachHang dangKyKhachHang(String tenDangNhap, String email, String matKhauHash) {
        if (nguoiDungRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new IllegalArgumentException("Ten dang nhap da ton tai");
        }
        if (nguoiDungRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email da ton tai");
        }

        KhachHang khachHang = KhachHang.builder()
                .tenDangNhap(tenDangNhap)
                .email(email)
                .matKhauHash(passwordEncoder.encode(matKhauHash))
                .vaiTro(VaiTro.KHACH_HANG)
                .trangThaiHoatDong(true)
                .build();

        return khachHangRepository.save(khachHang);
    }

    @Transactional(readOnly = true)
    public Optional<NguoiDung> timTheoTenDangNhap(String tenDangNhap) {
        return nguoiDungRepository.findByTenDangNhap(tenDangNhap);
    }

    @Transactional(readOnly = true)
    public Optional<NguoiDung> timTheoEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<NguoiDung> timTheoId(UUID id) {
        return nguoiDungRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<NguoiDung> danhSachKhachHang() {
        return nguoiDungRepository.findByVaiTro(VaiTro.KHACH_HANG);
    }

    @Transactional(readOnly = true)
    public List<NguoiDung> danhSachNhanVien() {
        return nguoiDungRepository.findByVaiTroIn(List.of(VaiTro.NHAN_VIEN_BAN_HANG, VaiTro.QUAN_LY_KHO));
    }

    @Transactional
    public NguoiDung khoaMoTaiKhoan(UUID id, boolean kichHoat) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan"));
        nguoiDung.setTrangThaiHoatDong(kichHoat);
        return nguoiDungRepository.save(nguoiDung);
    }

    @Transactional
    public NguoiDung luuNguoiDung(NguoiDung nguoiDung) {
        return nguoiDungRepository.save(nguoiDung);
    }

    @Transactional
    public NhanVienBanHang taoNhanVien(String tenDangNhap, String email, String matKhau) {
        if (nguoiDungRepository.existsByTenDangNhap(tenDangNhap) || nguoiDungRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Tai khoan da ton tai");
        }
        NhanVienBanHang nhanVien = NhanVienBanHang.builder()
                .tenDangNhap(tenDangNhap)
                .email(email)
                .matKhauHash(passwordEncoder.encode(matKhau))
                .vaiTro(VaiTro.NHAN_VIEN_BAN_HANG)
                .trangThaiHoatDong(true)
                .build();
        return nhanVienBanHangRepository.save(nhanVien);
    }

    @Transactional
    public NhanVienBanHang taoNhanVien(String tenDangNhap, String email, String matKhau, VaiTro vaiTro) {
        NhanVienBanHang nhanVien = taoNhanVien(tenDangNhap, email, matKhau);
        if (vaiTro != null && (vaiTro == VaiTro.NHAN_VIEN_BAN_HANG || vaiTro == VaiTro.QUAN_LY_KHO)) {
            nhanVien.setVaiTro(vaiTro);
        }
        return nhanVienBanHangRepository.save(nhanVien);
    }

    @Transactional
    public NguoiDung capNhatNhanVien(UUID id, String email, VaiTro vaiTro, Boolean kichHoat) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
        if (email != null && !email.isBlank()) {
            nguoiDung.setEmail(email.trim());
        }
        if (vaiTro != null && (vaiTro == VaiTro.NHAN_VIEN_BAN_HANG || vaiTro == VaiTro.QUAN_LY_KHO)) {
            nguoiDung.setVaiTro(vaiTro);
        }
        if (kichHoat != null) {
            nguoiDung.setTrangThaiHoatDong(kichHoat);
        }
        return nguoiDungRepository.save(nguoiDung);
    }

    @Transactional
    public void xoaNguoiDung(UUID id) {
        nguoiDungRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NguoiDung> tatCaNguoiDung() {
        return new ArrayList<>(nguoiDungRepository.findAll());
    }

    @Transactional
    public void doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi, String xacNhanMatKhau) {
        if (matKhauMoi == null || matKhauMoi.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp");
        }

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        if (matKhauCu == null || !passwordEncoder.matches(matKhauCu, nguoiDung.getMatKhauHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        nguoiDung.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);
    }
}

