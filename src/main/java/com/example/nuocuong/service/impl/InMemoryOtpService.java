package com.example.nuocuong.service.impl;

import com.example.nuocuong.service.FakeMailService;
import com.example.nuocuong.service.OtpService;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * OTP Email giả lập.
 *
 * Pattern liên quan:
 * - SRP: class này chỉ chịu trách nhiệm sinh/xác thực OTP.
 * - DIP: phụ thuộc vào FakeMailService (interface) thay vì cụ thể.
 */
@Service
public class InMemoryOtpService implements OtpService {
	private record OtpEntry(String otp, long expiresAtEpochMs) {
	}

	private final SecureRandom random = new SecureRandom();
	private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();
	private final FakeMailService fakeMailService;
	private final int otpLength;
	private final Duration ttl;
	private final String from;

	public InMemoryOtpService(
		FakeMailService fakeMailService,
		@Value("${app.otp.length:6}") int otpLength,
		@Value("${app.otp.ttl-seconds:300}") long ttlSeconds,
		@Value("${app.otp.from:no-reply@nuocuong.local}") String from
	) {
		this.fakeMailService = fakeMailService;
		this.otpLength = otpLength;
		this.ttl = Duration.ofSeconds(ttlSeconds);
		this.from = from;
	}

	@Override
	public void guiOtpDangKy(String email) {
		String otp = taoOtp();
		long expiresAt = System.currentTimeMillis() + ttl.toMillis();
		store.put(normalize(email), new OtpEntry(otp, expiresAt));

		// Email giả lập: đẩy vào "inbox" để UI đọc.
		fakeMailService.send(
			email,
			"[NuocUong] OTP đăng ký",
			"From: " + from + "\n\nMã OTP của bạn là: " + otp + "\nHết hạn sau " + ttl.toSeconds() + " giây."
		);
	}

	@Override
	public boolean xacThucOtp(String email, String otp) {
		String key = normalize(email);
		OtpEntry entry = store.get(key);
		if (entry == null) return false;
		if (System.currentTimeMillis() > entry.expiresAtEpochMs()) {
			store.remove(key);
			return false;
		}
		boolean ok = entry.otp().equals(otp);
		if (ok) store.remove(key);
		return ok;
	}

	private String taoOtp() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < otpLength; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	private String normalize(String email) {
		return email == null ? "" : email.trim().toLowerCase();
	}
}

