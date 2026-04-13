package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.PasswordResetOtp;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {
    Optional<PasswordResetOtp> findTopByNguoiDungIdAndDaSuDungFalseOrderByHetHanLucDesc(UUID nguoiDungId);
    Optional<PasswordResetOtp> findTopByNguoiDungIdOrderByHetHanLucDesc(UUID nguoiDungId);
}

