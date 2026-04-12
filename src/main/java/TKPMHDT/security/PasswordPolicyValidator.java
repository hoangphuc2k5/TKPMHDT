package TKPMHDT.security;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    public static final int MIN_LENGTH = 6;

    /** Mô tả chính sách (đồng bộ với giao diện / JS). */
    public static final String POLICY_DESCRIPTION_VI =
            "Mật khẩu cần ít nhất 6 ký tự, gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số. Không bắt buộc ký tự đặc biệt.";

    public void validateOrThrow(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu là bắt buộc");
        }
        if (rawPassword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Mật khẩu phải có tối thiểu " + MIN_LENGTH + " ký tự");
        }
        if (!rawPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 chữ hoa");
        }
        if (!rawPassword.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 chữ thường");
        }
        if (!rawPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 chữ số");
        }
    }
}
