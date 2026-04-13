package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.PasswordHistory;
import TKPMHDT.Entity.nguoidung.PasswordResetOtp;
import TKPMHDT.Repository.nguoidung.PasswordHistoryRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Repository.nguoidung.PasswordResetOtpRepository;
import TKPMHDT.Service.mail.EmailService;
import TKPMHDT.security.PasswordPolicyValidator;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuenMatKhauService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public QuenMatKhauService(
            NguoiDungRepository nguoiDungRepository,
            PasswordResetOtpRepository passwordResetOtpRepository,
            PasswordHistoryRepository passwordHistoryRepository,
            PasswordEncoder passwordEncoder,
            PasswordPolicyValidator passwordPolicyValidator,
            EmailService emailService
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
        this.emailService = emailService;
    }

    @Transactional
    public void taoVaGuiOtp(String email) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay email"));

        LocalDateTime now = LocalDateTime.now();
        long ttlMinutes = 5;
        passwordResetOtpRepository.findTopByNguoiDungIdOrderByHetHanLucDesc(nguoiDung.getId())
                .ifPresent(otpGanNhat -> {
                    if (otpGanNhat.getHetHanLuc().isAfter(now)) {
                        throw new IllegalArgumentException(
                                "Bạn chỉ có thể nhận 1 OTP mỗi 5 phút. Vui lòng thử lại sau ít phút.");
                    }
                });

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        PasswordResetOtp record = PasswordResetOtp.builder()
                .nguoiDung(nguoiDung)
                .otpCode(otp)
                .hetHanLuc(now.plusMinutes(ttlMinutes))
                .daSuDung(false)
                .build();
        passwordResetOtpRepository.save(record);

        try {
            emailService.sendOtpEmail(email, otp, ttlMinutes);
        } catch (MailException ex) {
            throw new IllegalStateException("Gui OTP that bai. Vui long kiem tra cau hinh email (SMTP).");
        }
    }

    @Transactional
    public void xacThucOtpVaDatLaiMatKhau(String email, String otp, String matKhauMoi) {
        passwordPolicyValidator.validateOrThrow(matKhauMoi);
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay email"));

        PasswordResetOtp record = passwordResetOtpRepository
                .findTopByNguoiDungIdAndDaSuDungFalseOrderByHetHanLucDesc(nguoiDung.getId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay OTP hop le"));

        if (record.getHetHanLuc().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP da het han");
        }
        if (!record.getOtpCode().equals(otp)) {
            throw new IllegalArgumentException("OTP khong dung");
        }
        if (passwordEncoder.matches(matKhauMoi, nguoiDung.getMatKhauHash())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng mật khẩu hiện tại");
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
        record.setDaSuDung(true);
        nguoiDungRepository.save(nguoiDung);
        passwordResetOtpRepository.save(record);
    }
}

