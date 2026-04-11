package TKPMHDT.security;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    public void validateOrThrow(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu là bắt buộc");
        }
        if (rawPassword.length() < 12) {
            throw new IllegalArgumentException("Mật khẩu phải có tối thiểu 12 ký tự");
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
        if (!rawPassword.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt");
        }
    }
}
