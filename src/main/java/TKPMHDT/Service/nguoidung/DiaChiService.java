package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Repository.nguoidung.DiaChiRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiaChiService {

    private final DiaChiRepository diaChiRepository;
    private final KhachHangRepository khachHangRepository;

    public DiaChiService(DiaChiRepository diaChiRepository, KhachHangRepository khachHangRepository) {
        this.diaChiRepository = diaChiRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @Transactional
    public DiaChi themDiaChi(UUID khachHangId, DiaChi diaChi) {
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));
        diaChi.setKhachHang(khachHang);
        return diaChiRepository.save(diaChi);
    }

    @Transactional(readOnly = true)
    public List<DiaChi> layDiaChiCuaKhachHang(UUID khachHangId) {
        return diaChiRepository.findByKhachHangId(khachHangId);
    }

    @Transactional
    public void xoaDiaChi(UUID diaChiId) {
        diaChiRepository.deleteById(diaChiId);
    }
}
