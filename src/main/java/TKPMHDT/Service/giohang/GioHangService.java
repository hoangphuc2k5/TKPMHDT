package TKPMHDT.Service.giohang;

import TKPMHDT.DTO.request.ToppingRequest;
import TKPMHDT.Entity.giohang.ChiTietGioHang;
import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.sanpham.ChiTietTopping;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import TKPMHDT.Repository.giohang.GioHangRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Service.khuyenmai.KhuyenMaiGiaSanPhamService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final KhuyenMaiGiaSanPhamService khuyenMaiGiaSanPhamService;

    public GioHangService(
            GioHangRepository gioHangRepository,
            KhachHangRepository khachHangRepository,
            NuocUongSanRepository nuocUongSanRepository,
            NguyenLieuRepository nguyenLieuRepository,
            KhuyenMaiGiaSanPhamService khuyenMaiGiaSanPhamService
    ) {
        this.gioHangRepository = gioHangRepository;
        this.khachHangRepository = khachHangRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.khuyenMaiGiaSanPhamService = khuyenMaiGiaSanPhamService;
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

    /**
     * Tải giỏ và cập nhật {@code thanhTien} từng dòng theo khuyến mãi giá hiện tại (và topping),
     * để món thêm trước khi có KM hoặc khi KM thay đổi vẫn hiển thị đúng trên UI giỏ hàng.
     */
    @Transactional
    public GioHang layVaDongBoGiaTheoKhuyenMai(UUID khachHangId) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        if (gioHang.getCacMatHang() == null || gioHang.getCacMatHang().isEmpty()) {
            return gioHang;
        }
        boolean dirty = false;
        for (ChiTietGioHang item : gioHang.getCacMatHang()) {
            BigDecimal moi = tinhThanhTienDong(item);
            if (item.getThanhTien() == null || item.getThanhTien().compareTo(moi) != 0) {
                item.setThanhTien(moi);
                dirty = true;
            }
        }
        if (dirty) {
            capNhatTongTien(gioHang);
            return gioHangRepository.save(gioHang);
        }
        return gioHang;
    }

    @Transactional
    public GioHang themMatHang(UUID khachHangId, UUID nuocUongId, int soLuong, Integer mucDa, String ghiChu, List<ToppingRequest> toppings) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }

        GioHang gioHang = layHoacTaoGioHang(khachHangId);
        NuocUongSan nuocUong = nuocUongSanRepository.findById(nuocUongId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nuoc uong"));

        List<ChiTietTopping> toppingEntities = buildToppingEntities(toppings);
        BigDecimal finalUnitPrice = tinhDonGiaDonViSauKmVaTopping(nuocUong, toppingEntities);

        TuyChinhKhachHang tuyChinhMoi = TuyChinhKhachHang.builder()
                .mucDa(mucDa)
                .ghiChu(ghiChu)
                .build();

        Optional<ChiTietGioHang> tonTai = gioHang.getCacMatHang()
                .stream()
                .filter(i -> cungMotCauHinhMatHang(i, nuocUongId, tuyChinhMoi, toppingEntities))
                .findFirst();

        if (tonTai.isPresent()) {
            ChiTietGioHang item = tonTai.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            item.setTuyChinh(cloneTuyChinh(tuyChinhMoi));
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
                    .tuyChinh(cloneTuyChinh(tuyChinhMoi))
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
    public GioHang capNhatMatHang(UUID khachHangId, UUID chiTietGioHangId, int soLuong, Integer mucDa, String ghiChu, List<ToppingRequest> toppings) {
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
        BigDecimal finalUnitPrice = tinhDonGiaDonViSauKmVaTopping(nuocUong, toppingEntities);

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
                damBaoLaTopping(nguyenLieu);
                price = nguyenLieu.getGiaDonVi() != null ? nguyenLieu.getGiaDonVi() : price;
                ten = nguyenLieu.getTen();
            } else if (request.getToppingId() != null) {
                nguyenLieu = nguyenLieuRepository.findById(request.getToppingId())
                        .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguyen lieu topping (toppingId)"));
                damBaoLaTopping(nguyenLieu);
                price = nguyenLieu.getGiaDonVi() != null ? nguyenLieu.getGiaDonVi() : price;
                ten = nguyenLieu.getTen();
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

    private BigDecimal tinhDonGiaDonViSauKmVaTopping(NuocUongSan nuocUong, List<ChiTietTopping> toppingEntities) {
        BigDecimal unitPrice = khuyenMaiGiaSanPhamService.donGiaCoSoSauKhuyenMai(nuocUong);
        List<ChiTietTopping> tops = toppingEntities != null ? toppingEntities : List.of();
        BigDecimal toppingTotal = tops.stream()
                .map(t -> (t.getDonGia() != null ? t.getDonGia() : BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(t.getSoLuong() == null ? 1 : t.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return unitPrice.add(toppingTotal);
    }

    private BigDecimal tinhThanhTienDong(ChiTietGioHang item) {
        int sl = item.getSoLuong() == null || item.getSoLuong() <= 0 ? 1 : item.getSoLuong();
        List<ChiTietTopping> tops = item.getToppings() != null ? item.getToppings() : List.of();
        return tinhDonGiaDonViSauKmVaTopping(item.getNuocUong(), tops)
                .multiply(BigDecimal.valueOf(sl));
    }

    private static TuyChinhKhachHang cloneTuyChinh(TuyChinhKhachHang src) {
        if (src == null) {
            return null;
        }
        return TuyChinhKhachHang.builder()
                .mucDa(src.getMucDa())
                .ghiChu(src.getGhiChu())
                .build();
    }

    /** Cùng đồ uống + cùng tùy chỉnh + cùng topping → gộp số lượng; khác topping → dòng riêng. */
    private boolean cungMotCauHinhMatHang(
            ChiTietGioHang item,
            UUID nuocUongId,
            TuyChinhKhachHang tuyChinhMoi,
            List<ChiTietTopping> toppingMoi) {
        if (!item.getNuocUong().getId().equals(nuocUongId)) {
            return false;
        }
        if (!tuyChinhGiongNhau(item.getTuyChinh(), tuyChinhMoi)) {
            return false;
        }
        List<ChiTietTopping> toppingCu = item.getToppings() != null ? item.getToppings() : new ArrayList<>();
        return toppingListsGiongNhau(toppingCu, toppingMoi != null ? toppingMoi : new ArrayList<>());
    }

    private static boolean tuyChinhGiongNhau(TuyChinhKhachHang a, TuyChinhKhachHang b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return Objects.equals(a.getMucDa(), b.getMucDa())
                && ((a.getGhiChu() == null && b.getGhiChu() == null)
                        || (a.getGhiChu() != null && a.getGhiChu().equals(b.getGhiChu())));
    }

    private static boolean toppingListsGiongNhau(List<ChiTietTopping> toppings1, List<ChiTietTopping> toppings2) {
        if (toppings1.size() != toppings2.size()) {
            return false;
        }
        for (ChiTietTopping t1 : toppings1) {
            boolean foundMatch = false;
            for (ChiTietTopping t2 : toppings2) {
                boolean sameIngredientId;
                if (t1.getNguyenLieu() != null && t2.getNguyenLieu() != null) {
                    sameIngredientId = Objects.equals(t1.getNguyenLieu().getId(), t2.getNguyenLieu().getId());
                } else if (t1.getNguyenLieu() == null && t2.getNguyenLieu() == null) {
                    sameIngredientId = Objects.equals(
                            t1.getTen() != null ? t1.getTen() : "",
                            t2.getTen() != null ? t2.getTen() : "");
                } else {
                    sameIngredientId = false;
                }

                BigDecimal donGia1 = t1.getDonGia() != null ? t1.getDonGia() : BigDecimal.ZERO;
                BigDecimal donGia2 = t2.getDonGia() != null ? t2.getDonGia() : BigDecimal.ZERO;
                int soLuong1 = t1.getSoLuong() != null ? t1.getSoLuong() : 1;
                int soLuong2 = t2.getSoLuong() != null ? t2.getSoLuong() : 1;

                if (sameIngredientId && soLuong1 == soLuong2 && donGia1.compareTo(donGia2) == 0) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                return false;
            }
        }
        return true;
    }

    private static void damBaoLaTopping(NguyenLieu nl) {
        if (nl == null || nl.getLoaiNguyenLieu() != LoaiNguyenLieu.TOPPING) {
            throw new IllegalArgumentException("Chi chap nhan nguyen lieu loai TOPPING lam topping");
        }
    }
}

