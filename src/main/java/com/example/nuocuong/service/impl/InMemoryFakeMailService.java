package com.example.nuocuong.service.impl;

import com.example.nuocuong.service.FakeMailService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InMemoryFakeMailService implements FakeMailService {
	private final List<FakeMail> store = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void send(String to, String subject, String body) {
		store.add(new FakeMail(to, subject, body, System.currentTimeMillis()));
	}

	@Override
	public List<FakeMail> inbox() {
		synchronized (store) {
			return List.copyOf(store);
		}
	}
}

