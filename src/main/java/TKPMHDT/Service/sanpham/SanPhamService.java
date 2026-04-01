package TKPMHDT.Service.sanpham;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SanPhamService {

    private final NuocUongSanRepository nuocUongSanRepository;
    private final NguyenLieuRepository nguyenLieuRepository;

    public SanPhamService(
            NuocUongSanRepository nuocUongSanRepository,
            NguyenLieuRepository nguyenLieuRepository
    ) {
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
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
}

