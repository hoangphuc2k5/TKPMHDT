package com.example.nuocuong.service;

import java.util.List;

public interface FakeMailService {
	void send(String to, String subject, String body);

	List<FakeMail> inbox();

	record FakeMail(String to, String subject, String body, long sentAtEpochMs) {
	}
}

