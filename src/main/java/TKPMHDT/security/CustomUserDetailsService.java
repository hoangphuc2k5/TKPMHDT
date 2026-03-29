package TKPMHDT.security;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    public CustomUserDetailsService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay tai khoan: " + username));

        String roleName = "ROLE_" + nguoiDung.getVaiTro().name();
        return User.withUsername(nguoiDung.getTenDangNhap())
                .password(nguoiDung.getMatKhauHash())
                .authorities(List.of(new SimpleGrantedAuthority(roleName)))
                .disabled(!nguoiDung.isTrangThaiHoatDong())
                .build();
    }
}

