package TKPMHDT.Service.nguoidung;

import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Repository.nguoidung.DiaChiRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import java.util.List;
import java.util.Optional;
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

        List<DiaChi> diaChiHienCo = diaChiRepository.findByKhachHangId(khachHangId);
        if (diaChiHienCo.isEmpty()) {
            diaChi.setLaMacDinh(true);
        } else if (diaChi.isLaMacDinh()) {
            diaChiHienCo.forEach(dc -> dc.setLaMacDinh(false));
            diaChiRepository.saveAll(diaChiHienCo);
        }

        diaChi.setKhachHang(khachHang);
        return diaChiRepository.save(diaChi);
    }

    @Transactional(readOnly = true)
    public List<DiaChi> layDiaChiCuaKhachHang(UUID khachHangId) {
        return diaChiRepository.findByKhachHangId(khachHangId);
    }

    @Transactional
    public DiaChi capNhatDiaChi(UUID khachHangId, UUID diaChiId, DiaChi duLieu) {
        DiaChi hienTai = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ"));
        if (!hienTai.getKhachHang().getId().equals(khachHangId)) {
            throw new IllegalArgumentException("Địa chỉ không thuộc khách hàng này");
        }
        if (duLieu.getTenNguoiNhan() != null && !duLieu.getTenNguoiNhan().isBlank()) {
            hienTai.setTenNguoiNhan(duLieu.getTenNguoiNhan().trim());
        }
        if (duLieu.getSoDienThoai() != null && !duLieu.getSoDienThoai().isBlank()) {
            hienTai.setSoDienThoai(duLieu.getSoDienThoai().trim());
        }
        if (duLieu.getDiaChiCuThe() != null && !duLieu.getDiaChiCuThe().isBlank()) {
            hienTai.setDiaChiCuThe(duLieu.getDiaChiCuThe().trim());
        }
        if (duLieu.getPhuongXa() != null && !duLieu.getPhuongXa().isBlank()) {
            hienTai.setPhuongXa(duLieu.getPhuongXa().trim());
        }
        if (duLieu.getQuanHuyen() != null && !duLieu.getQuanHuyen().isBlank()) {
            hienTai.setQuanHuyen(duLieu.getQuanHuyen().trim());
        }
        if (duLieu.getTinhThanhPho() != null && !duLieu.getTinhThanhPho().isBlank()) {
            hienTai.setTinhThanhPho(duLieu.getTinhThanhPho().trim());
        }
        return diaChiRepository.save(hienTai);
    }

    @Transactional
    public void xoaDiaChi(UUID diaChiId) {
        DiaChi diaChi = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ"));
        UUID khachHangId = diaChi.getKhachHang().getId();
        boolean dangLaMacDinh = diaChi.isLaMacDinh();
        diaChiRepository.delete(diaChi);

        if (dangLaMacDinh) {
            List<DiaChi> conLai = diaChiRepository.findByKhachHangId(khachHangId);
            if (!conLai.isEmpty()) {
                DiaChi moi = conLai.get(0);
                moi.setLaMacDinh(true);
                diaChiRepository.save(moi);
            }
        }
    }

    @Transactional
    public DiaChi datDiaChiMacDinh(UUID khachHangId, UUID diaChiId) {
        List<DiaChi> danhSach = diaChiRepository.findByKhachHangId(khachHangId);
        if (danhSach.isEmpty()) {
            throw new IllegalArgumentException("Khách hàng chưa có địa chỉ");
        }

        DiaChi diaChiCanDat = danhSach.stream()
                .filter(dc -> dc.getId().equals(diaChiId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không thuộc khách hàng"));

        danhSach.forEach(dc -> dc.setLaMacDinh(dc.getId().equals(diaChiCanDat.getId())));
        diaChiRepository.saveAll(danhSach);
        return diaChiCanDat;
    }

    @Transactional(readOnly = true)
    public Optional<DiaChi> layDiaChiMacDinh(UUID khachHangId) {
        return diaChiRepository.findFirstByKhachHangIdAndLaMacDinhTrue(khachHangId);
    }
}
