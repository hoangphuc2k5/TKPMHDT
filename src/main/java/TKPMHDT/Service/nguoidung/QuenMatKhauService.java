package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.PasswordResetOtp;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Repository.nguoidung.PasswordResetOtpRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuenMatKhauService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public QuenMatKhauService(
            NguoiDungRepository nguoiDungRepository,
            PasswordResetOtpRepository passwordResetOtpRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void taoVaGuiOtp(String email) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay email"));

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        PasswordResetOtp record = PasswordResetOtp.builder()
                .nguoiDung(nguoiDung)
                .otpCode(otp)
                .hetHanLuc(LocalDateTime.now().plusMinutes(5))
                .daSuDung(false)
                .build();
        passwordResetOtpRepository.save(record);

        // TODO: thay bang gui mail thuc te.
        System.out.println("OTP reset mat khau cho " + email + ": " + otp);
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

