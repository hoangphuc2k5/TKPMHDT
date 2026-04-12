package TKPMHDT.Service.giohang;

import TKPMHDT.DTO.request.ToppingRequest;
import TKPMHDT.Entity.giohang.ChiTietGioHang;
import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.sanpham.ChiTietTopping;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import TKPMHDT.Entity.sanpham.TuyChonTuyChinh;
import TKPMHDT.Repository.giohang.GioHangRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Repository.sanpham.TuyChonTuyChinhRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private final NguyenLieuRepository nguyenLieuRepository;
    private final TuyChonTuyChinhRepository tuyChonTuyChinhRepository;

    public GioHangService(
            GioHangRepository gioHangRepository,
            KhachHangRepository khachHangRepository,
            NuocUongSanRepository nuocUongSanRepository,
            NguyenLieuRepository nguyenLieuRepository,
            TuyChonTuyChinhRepository tuyChonTuyChinhRepository
    ) {
        this.gioHangRepository = gioHangRepository;
        this.khachHangRepository = khachHangRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.tuyChonTuyChinhRepository = tuyChonTuyChinhRepository;
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
    public GioHang themMatHang(UUID khachHangId, UUID nuocUongId, int soLuong, Integer mucDuong, Integer mucDa, String ghiChu, List<ToppingRequest> toppings) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }

        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        NuocUongSan nuocUong = nuocUongSanRepository.findById(nuocUongId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nuoc uong"));

        List<ChiTietTopping> toppingEntities = buildToppingEntities(toppings);
        BigDecimal unitPrice = nuocUong.getGia();
        BigDecimal toppingTotal = toppingEntities.stream()
                .map(t -> t.getDonGia().multiply(BigDecimal.valueOf(t.getSoLuong() == null ? 1 : t.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal finalUnitPrice = unitPrice.add(toppingTotal);

        Optional<ChiTietGioHang> tonTai = gioHang.getCacMatHang()
                .stream()
                .filter(i -> i.getNuocUong().getId().equals(nuocUongId))
                .findFirst();

        if (tonTai.isPresent()) {
            ChiTietGioHang item = tonTai.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            item.setTuyChinh(TuyChinhKhachHang.builder()
                    .mucDa(mucDa)
                    .ghiChu(ghiChu)
                    .build());
            item.getToppings().clear();
            item.getToppings().addAll(toppingEntities);
            toppingEntities.forEach(t -> t.setChiTietGioHang(item));
            item.setThanhTien(finalUnitPrice.multiply(BigDecimal.valueOf(item.getSoLuong())));
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
                    .thanhTien(finalUnitPrice.multiply(BigDecimal.valueOf(soLuong)))
                    .duocChonThanhToan(true)
                    .build();
            itemMoi.getToppings().addAll(toppingEntities);
            toppingEntities.forEach(t -> t.setChiTietGioHang(itemMoi));
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
    public GioHang capNhatMatHang(UUID khachHangId, UUID chiTietGioHangId, int soLuong, Integer mucDuong, Integer mucDa, String ghiChu, List<ToppingRequest> toppings) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }

        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        ChiTietGioHang item = gioHang.getCacMatHang().stream()
                .filter(i -> i.getId().equals(chiTietGioHangId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay mat hang trong gio"));

        NuocUongSan nuocUong = item.getNuocUong();
        List<ChiTietTopping> toppingEntities = buildToppingEntities(toppings);
        BigDecimal unitPrice = nuocUong.getGia();
        BigDecimal toppingTotal = toppingEntities.stream()
                .map(t -> t.getDonGia().multiply(BigDecimal.valueOf(t.getSoLuong() == null ? 1 : t.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal finalUnitPrice = unitPrice.add(toppingTotal);

        item.setSoLuong(soLuong);
        item.setTuyChinh(TuyChinhKhachHang.builder()
                .mucDa(mucDa)
                .ghiChu(ghiChu)
                .build());
        item.getToppings().clear();
        item.getToppings().addAll(toppingEntities);
        toppingEntities.forEach(t -> t.setChiTietGioHang(item));
        item.setThanhTien(finalUnitPrice.multiply(BigDecimal.valueOf(soLuong)));
        item.setDuocChonThanhToan(true);

        capNhatTongTien(gioHang);
        return gioHangRepository.save(gioHang);
    }

    private List<ChiTietTopping> buildToppingEntities(List<ToppingRequest> toppingRequests) {
        List<ChiTietTopping> entities = new ArrayList<>();
        if (toppingRequests == null || toppingRequests.isEmpty()) {
            return entities;
        }

        for (ToppingRequest request : toppingRequests) {
            if (request == null) {
                continue;
            }

            NguyenLieu nguyenLieu = null;
            BigDecimal price = BigDecimal.valueOf(5000);
            String ten = null;
            int quantity = request.getSoLuong() == null || request.getSoLuong() <= 0 ? 1 : request.getSoLuong();

            if (request.getNguyenLieuId() != null) {
                nguyenLieu = nguyenLieuRepository.findById(request.getNguyenLieuId())
                        .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguyen lieu topping"));
                price = nguyenLieu.getGiaDonVi() != null ? nguyenLieu.getGiaDonVi() : price;
                ten = nguyenLieu.getTen();
            } else if (request.getToppingId() != null) {
                TuyChonTuyChinh option = tuyChonTuyChinhRepository.findById(request.getToppingId())
                        .orElseThrow(() -> new IllegalArgumentException("Khong tim thay topping option"));
                price = option.getGiaThem() != null ? option.getGiaThem() : price;
                ten = option.getTen();
            } else if (request.getGiaThem() != null) {
                price = request.getGiaThem();
                ten = request.getTen();
            } else {
                continue;
            }

            ChiTietTopping topping = ChiTietTopping.builder()
                    .nguyenLieu(nguyenLieu)
                    .soLuong(quantity)
                    .donGia(price)
                    .ten(ten)
                    .build();
            entities.add(topping);
        }
        return entities;
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

