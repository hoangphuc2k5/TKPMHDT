package TKPMHDT.Service.giohang;

import TKPMHDT.Entity.giohang.ChiTietGioHang;
import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import TKPMHDT.Repository.giohang.GioHangRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final NuocUongSanRepository nuocUongSanRepository;

    public GioHangService(
            GioHangRepository gioHangRepository,
            KhachHangRepository khachHangRepository,
            NuocUongSanRepository nuocUongSanRepository
    ) {
        this.gioHangRepository = gioHangRepository;
        this.khachHangRepository = khachHangRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
    }

    @Transactional
    public GioHang layHoacTaoGioHang(UUID khachHangId) {
        return gioHangRepository.findByKhachHangId(khachHangId)
                .orElseGet(() -> {
                    KhachHang khachHang = khachHangRepository.findById(khachHangId)
                            .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khach hang"));
                    GioHang gioHang = GioHang.builder()
                            .khachHang(khachHang)
                            .tongTien(BigDecimal.ZERO)
                            .build();
                    return gioHangRepository.save(gioHang);
                });
    }

    @Transactional
    public GioHang themMatHang(UUID khachHangId, UUID nuocUongId, int soLuong, Integer mucDuong, Integer mucDa, String ghiChu) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }

        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        NuocUongSan nuocUong = nuocUongSanRepository.findById(nuocUongId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nuoc uong"));

        Optional<ChiTietGioHang> tonTai = gioHang.getCacMatHang()
                .stream()
                .filter(i -> i.getNuocUong().getId().equals(nuocUongId))
                .findFirst();

        if (tonTai.isPresent()) {
            ChiTietGioHang item = tonTai.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            item.setThanhTien(nuocUong.getGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            item.setTuyChinh(TuyChinhKhachHang.builder()                  
                    .mucDa(mucDa)
                    .ghiChu(ghiChu)
                    .build());
            item.setDuocChonThanhToan(true);
        } else {
            ChiTietGioHang itemMoi = ChiTietGioHang.builder()
                    .gioHang(gioHang)
                    .nuocUong(nuocUong)
                    .soLuong(soLuong)
                    .tuyChinh(TuyChinhKhachHang.builder()                          
                            .mucDa(mucDa)
                            .ghiChu(ghiChu)
                            .build())
                    .thanhTien(nuocUong.getGia().multiply(BigDecimal.valueOf(soLuong)))
                    .duocChonThanhToan(true)
                    .build();
            gioHang.getCacMatHang().add(itemMoi);
        }

        capNhatTongTien(gioHang);
        return gioHangRepository.save(gioHang);
    }

    @Transactional
    public GioHang xoaMatHang(UUID khachHangId, UUID chiTietGioHangId) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        gioHang.getCacMatHang().removeIf(i -> i.getId().equals(chiTietGioHangId));
        capNhatTongTien(gioHang);
        return gioHangRepository.save(gioHang);
    }

    @Transactional
    public GioHang xoaHet(UUID khachHangId) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        gioHang.getCacMatHang().clear();
        gioHang.setTongTien(BigDecimal.ZERO);
        return gioHangRepository.save(gioHang);
    }

    @Transactional
    public GioHang capNhatChonMatHang(UUID khachHangId, UUID chiTietGioHangId, boolean duocChon) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        ChiTietGioHang matHang = gioHang.getCacMatHang().stream()
                .filter(i -> i.getId().equals(chiTietGioHangId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay mat hang trong gio"));
        matHang.setDuocChonThanhToan(duocChon);
        return gioHangRepository.save(gioHang);
    }

    @Transactional
    public GioHang capNhatChonTatCa(UUID khachHangId, boolean duocChon) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        gioHang.getCacMatHang().forEach(i -> i.setDuocChonThanhToan(duocChon));
        return gioHangRepository.save(gioHang);
    }

    @Transactional(readOnly = true)
    public List<ChiTietGioHang> layMatHangDuocChon(UUID khachHangId) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        return gioHang.getCacMatHang().stream()
                .filter(ChiTietGioHang::isDuocChonThanhToan)
                .toList();
    }

    private void capNhatTongTien(GioHang gioHang) {
        BigDecimal tong = gioHang.getCacMatHang()
                .stream()
                .map(ChiTietGioHang::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        gioHang.setTongTien(tong);
    }
}

