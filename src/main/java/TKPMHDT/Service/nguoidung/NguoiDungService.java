package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NhanVienBanHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.PasswordHistory;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.nguoidung.NhanVienBanHangRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Repository.nguoidung.PasswordHistoryRepository;
import TKPMHDT.security.PasswordPolicyValidator;
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
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;

    public NguoiDungService(
            NguoiDungRepository nguoiDungRepository,
            KhachHangRepository khachHangRepository,
            NhanVienBanHangRepository nhanVienBanHangRepository,
            PasswordHistoryRepository passwordHistoryRepository,
            PasswordEncoder passwordEncoder,
            PasswordPolicyValidator passwordPolicyValidator
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienBanHangRepository = nhanVienBanHangRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    @Transactional
    public KhachHang dangKyKhachHang(String tenDangNhap, String email, String matKhauHash) {
        passwordPolicyValidator.validateOrThrow(matKhauHash);
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

        KhachHang saved = khachHangRepository.save(khachHang);
        passwordHistoryRepository.save(PasswordHistory.builder()
                .nguoiDung(saved)
                .matKhauHash(saved.getMatKhauHash())
                .build());
        return saved;
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
        return nguoiDungRepository.findByVaiTro(VaiTro.NHAN_VIEN_BAN_HANG);
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
        passwordPolicyValidator.validateOrThrow(matKhau);
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
        NhanVienBanHang saved = nhanVienBanHangRepository.save(nhanVien);
        passwordHistoryRepository.save(PasswordHistory.builder()
                .nguoiDung(saved)
                .matKhauHash(saved.getMatKhauHash())
                .build());
        return saved;
    }

    @Transactional
    public NhanVienBanHang taoNhanVien(String tenDangNhap, String email, String matKhau, VaiTro vaiTro) {
        if (vaiTro != null && vaiTro != VaiTro.NHAN_VIEN_BAN_HANG) {
            throw new IllegalArgumentException("Chi co the tao tai khoan nhan vien ban hang");
        }
        return taoNhanVien(tenDangNhap, email, matKhau);
    }

    @Transactional
    public NguoiDung capNhatNhanVien(UUID id, String email, VaiTro vaiTro, Boolean kichHoat) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
        if (email != null && !email.isBlank()) {
            nguoiDung.setEmail(email.trim());
        }
        if (vaiTro != null) {
            if (vaiTro != VaiTro.NHAN_VIEN_BAN_HANG) {
                throw new IllegalArgumentException("Chi cap nhat duoc vai tro nhan vien ban hang");
            }
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
        passwordPolicyValidator.validateOrThrow(matKhauMoi);
        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp");
        }

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        if (matKhauCu == null || !passwordEncoder.matches(matKhauCu, nguoiDung.getMatKhauHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        boolean reused = passwordHistoryRepository.findTop3ByNguoiDungIdOrderByCreatedAtDesc(nguoiDung.getId()).stream()
                .anyMatch(h -> passwordEncoder.matches(matKhauMoi, h.getMatKhauHash()));
        if (reused) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng 3 mật khẩu gần nhất");
        }

        passwordHistoryRepository.save(PasswordHistory.builder()
                .nguoiDung(nguoiDung)
                .matKhauHash(nguoiDung.getMatKhauHash())
                .build());
        nguoiDung.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);
    }
}

