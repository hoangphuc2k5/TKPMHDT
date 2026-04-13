package TKPMHDT.security;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Repository.hethong.VaiTroQuyenRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroQuyenRepository vaiTroQuyenRepository;

    public CustomUserDetailsService(
            NguoiDungRepository nguoiDungRepository,
            VaiTroQuyenRepository vaiTroQuyenRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.vaiTroQuyenRepository = vaiTroQuyenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String dinhDanh = username == null ? "" : username.trim();
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(dinhDanh)
                .or(() -> nguoiDungRepository.findBySoDienThoai(dinhDanh))
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay tai khoan: " + username));

        String roleName = "ROLE_" + nguoiDung.getVaiTro().name();
        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(roleName));
        PermissionCatalog.resolvePermissions(
                        nguoiDung.getVaiTro(),
                        vaiTroQuyenRepository.findByVaiTro(nguoiDung.getVaiTro().name()).orElse(null))
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

        return User.withUsername(nguoiDung.getTenDangNhap())
                .password(nguoiDung.getMatKhauHash())
                .authorities(authorities)
                .disabled(!nguoiDung.isTrangThaiHoatDong())
                .build();
    }
}
