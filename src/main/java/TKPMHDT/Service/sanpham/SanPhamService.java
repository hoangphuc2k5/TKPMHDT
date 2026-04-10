package TKPMHDT.Service.sanpham;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Repository.sanpham.TuyChonTuyChinhRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SanPhamService {

	private final TuyChonTuyChinhRepository tuyChonRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final NguyenLieuRepository nguyenLieuRepository;	
    
    // sua contructor do them 1 field tuychonRepository
    public SanPhamService(
            NuocUongSanRepository nuocUongSanRepository,
            NguyenLieuRepository nguyenLieuRepository,
            TuyChonTuyChinhRepository tuyChonRepository
    ) {
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.tuyChonRepository = tuyChonRepository;
    }

    @Transactional(readOnly = true)
    public List<NuocUongSan> layDanhSachNuocUong() {
        return nuocUongSanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<NuocUongSan> timNuocUongTheoTen(String ten) {
        return nuocUongSanRepository.findByTenContainingIgnoreCase(ten);
    }

    @Transactional(readOnly = true)
    public Optional<NuocUongSan> layNuocUongTheoId(UUID id) {
        return nuocUongSanRepository.findById(id);
    }

    @Transactional
    public NuocUongSan luuNuocUong(NuocUongSan nuocUongSan) {
        return nuocUongSanRepository.save(nuocUongSan);
    }

    @Transactional
    public void xoaNuocUong(UUID id) {
        nuocUongSanRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NguyenLieu> layDanhSachNguyenLieu() {
        return nguyenLieuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<NguyenLieu> timNguyenLieuTheoTen(String ten) {
        return nguyenLieuRepository.findByTenContainingIgnoreCase(ten);
    }

    @Transactional
    public NguyenLieu luuNguyenLieu(NguyenLieu nguyenLieu) {
        return nguyenLieuRepository.save(nguyenLieu);
    }

    @Transactional(readOnly = true)
    public Optional<NguyenLieu> layNguyenLieuTheoId(UUID id) {
        return nguyenLieuRepository.findById(id);
    }

    @Transactional
    public void xoaNguyenLieu(UUID id) {
        nguyenLieuRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NguyenLieu> layNguyenLieuCanhBao() {
        return nguyenLieuRepository.findNguyenLieuCanhBao();
    }
    
    // Them api cho nay
    @Transactional(readOnly = true)
    public Map<String, Object> layChiTietDayDu(UUID id) {

        NuocUongSan sp = nuocUongSanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        Map<String, Object> data = new HashMap<>();

        data.put("sanPham", sp);
        data.put("size", tuyChonRepository.findByNhomIgnoreCase("SIZE"));
        data.put("duong", tuyChonRepository.findByNhomIgnoreCase("DUONG"));
        data.put("da", tuyChonRepository.findByNhomIgnoreCase("DA"));
        data.put("topping", tuyChonRepository.findByNhomIgnoreCase("TOPPING"));

        return data;
    }
}

