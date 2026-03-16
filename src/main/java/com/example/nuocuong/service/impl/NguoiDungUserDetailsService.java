package com.example.nuocuong.service.impl;

import com.example.nuocuong.entity.NguoiDung;
import com.example.nuocuong.repository.NguoiDungRepository;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class NguoiDungUserDetailsService implements UserDetailsService {
	private final NguoiDungRepository nguoiDungRepository;

	public NguoiDungUserDetailsService(NguoiDungRepository nguoiDungRepository) {
		this.nguoiDungRepository = nguoiDungRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		NguoiDung nd = nguoiDungRepository.findByEmail(username.trim().toLowerCase())
			.orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

		if (!nd.isKichHoat()) {
			throw new UsernameNotFoundException("Tài khoản chưa kích hoạt OTP");
		}

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + nd.getVaiTro().name()));
		return org.springframework.security.core.userdetails.User.builder()
			.username(nd.getEmail())
			.password(nd.getMatKhauMaHoa())
			.authorities(authorities)
			.accountLocked(false)
			.disabled(!nd.isKichHoat())
			.build();
	}
}

