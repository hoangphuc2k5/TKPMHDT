package TKPMHDT.Service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.from:${spring.mail.username:}}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendOtpEmail(String toEmail, String otp, long ttlMinutes) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromEmail != null && !fromEmail.isBlank()) {
            message.setFrom(fromEmail);
        }
        message.setTo(toEmail);
        message.setSubject("Mã OTP đặt lại mật khẩu");
        message.setText("""
                Bạn vừa yêu cầu đặt lại mật khẩu.

                Mã OTP của bạn: %s
                Mã có hiệu lực trong %d phút.

                Nếu bạn không yêu cầu thao tác này, vui lòng bỏ qua email.
                """.formatted(otp, ttlMinutes));

        mailSender.send(message);
    }
}

