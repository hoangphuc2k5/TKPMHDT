package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.PasswordResetOtp;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Repository.nguoidung.PasswordResetOtpRepository;
import TKPMHDT.Service.mail.EmailService;
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
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public QuenMatKhauService(
            NguoiDungRepository nguoiDungRepository,
            PasswordResetOtpRepository passwordResetOtpRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void taoVaGuiOtp(String email) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay email"));

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        long ttlMinutes = 5;
        PasswordResetOtp record = PasswordResetOtp.builder()
                .nguoiDung(nguoiDung)
                .otpCode(otp)
                .hetHanLuc(LocalDateTime.now().plusMinutes(ttlMinutes))
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

        nguoiDung.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        record.setDaSuDung(true);
        nguoiDungRepository.save(nguoiDung);
        passwordResetOtpRepository.save(record);
    }
}

