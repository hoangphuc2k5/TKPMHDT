package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.sanpham.CongThuc;
import TKPMHDT.Entity.sanpham.LuongNguyenLieu;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import TKPMHDT.Repository.sanpham.CongThucRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AdminSanPhamCongThucHelper {

    private final CongThucRepository congThucRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final SanPhamService sanPhamService;

    public AdminSanPhamCongThucHelper(
            CongThucRepository congThucRepository,
            NuocUongSanRepository nuocUongSanRepository,
            SanPhamService sanPhamService) {
        this.congThucRepository = congThucRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.sanPhamService = sanPhamService;
    }

    public void apDungCauHinhTuyChinhMacDinh(NuocUongSan sanPham) {
        sanPham.setMucDuongTuyChon("Không đường,Ít đường,Bình thường,Nhiều đường");
        sanPham.setMucDuongMacDinh("Bình thường");
        sanPham.setMucDaTuyChon("Không đá,Ít đá,Bình thường,Nhiều đá");
        sanPham.setMucDaMacDinh("Bình thường");
        sanPham.setKichCoTuyChon("Nhỏ,Vừa,Lớn");
        sanPham.setKichCoMacDinh("Vừa");
        sanPham.setCoApDungSize(true);
        sanPham.setToppingChoPhep("");
    }

    public CongThuc taoCongThucRiengChoSanPham(UUID congThucMauId, String tenSanPham, BigDecimal giaCoBan) {
        CongThuc congThucMau = null;
        if (congThucMauId != null) {
            congThucMau = congThucRepository
                    .findById(congThucMauId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công thức"));
        }
        CongThuc congThucMoi = CongThuc.builder()
                .ten("CT - " + (tenSanPham != null && !tenSanPham.isBlank() ? tenSanPham : "Sản phẩm"))
                .moTa("Công thức riêng cho sản phẩm")
                .giaCoBan(giaCoBan != null ? giaCoBan : BigDecimal.ZERO)
                .build();
        if (congThucMau != null && congThucMau.getLuongNguyenLieus() != null) {
            List<LuongNguyenLieu> danhSachSaoChep = congThucMau.getLuongNguyenLieus().stream()
                    .map(luong -> LuongNguyenLieu.builder()
                            .congThuc(congThucMoi)
                            .nguyenLieu(luong.getNguyenLieu())
                            .soLuong(luong.getSoLuong())
                            .donVi(luong.getDonVi())
                            .build())
                    .toList();
            congThucMoi.setLuongNguyenLieus(new ArrayList<>(danhSachSaoChep));
        }
        return congThucRepository.save(congThucMoi);
    }

    public CongThuc damBaoCongThucRieng(NuocUongSan sanPham, boolean saveProductIfChanged) {
        if (sanPham.getCongThucCoBan() == null) {
            CongThuc created = taoCongThucRiengChoSanPham(null, sanPham.getTen(), sanPham.getGia());
            sanPham.setCongThucCoBan(created);
            if (saveProductIfChanged) {
                sanPhamService.luuNuocUong(sanPham);
            }
            return created;
        }
        UUID congThucId = sanPham.getCongThucCoBan().getId();
        if (congThucId == null) {
            return sanPham.getCongThucCoBan();
        }
        long soSanPhamDungChung = nuocUongSanRepository.countByCongThucCoBanId(congThucId);
        if (soSanPhamDungChung <= 1) {
            return sanPham.getCongThucCoBan();
        }
        CongThuc cloned = taoCongThucRiengChoSanPham(congThucId, sanPham.getTen(), sanPham.getGia());
        sanPham.setCongThucCoBan(cloned);
        if (saveProductIfChanged) {
            sanPhamService.luuNuocUong(sanPham);
        }
        return cloned;
    }

    public Map<String, Object> toCongThucIngredientPayload(LuongNguyenLieu entity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", entity.getId());
        NguyenLieu nl = entity.getNguyenLieu();
        payload.put("nguyenLieuId", nl != null ? nl.getId() : null);
        payload.put("tenNguyenLieu", nl != null ? nl.getTen() : "");
        payload.put(
                "loaiNguyenLieu",
                nl != null && nl.getLoaiNguyenLieu() != null ? nl.getLoaiNguyenLieu().name() : "INGREDIENT");
        payload.put("soLuong", entity.getSoLuong());
        payload.put("donVi", entity.getDonVi());
        return payload;
    }

    public BigDecimal soLuongMacDinhChoCongThuc(NguyenLieu nl, BigDecimal tuRequest) {
        if (nl != null && nl.getLoaiNguyenLieu() == LoaiNguyenLieu.TOPPING) {
            return BigDecimal.ONE;
        }
        return tuRequest != null ? tuRequest : BigDecimal.ZERO;
    }

    public String donViMacDinhChoCongThuc(NguyenLieu nl, String tuRequest) {
        if (nl != null && nl.getLoaiNguyenLieu() == LoaiNguyenLieu.TOPPING) {
            return nl.getDonVi() != null && !nl.getDonVi().isBlank() ? nl.getDonVi() : "phần";
        }
        if (tuRequest != null && !tuRequest.isBlank()) {
            return tuRequest;
        }
        return nl != null && nl.getDonVi() != null ? nl.getDonVi() : "";
    }

    public List<String> parseCsv(String csv, List<String> defaults) {
        if (csv == null || csv.isBlank()) {
            return defaults;
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    public String joinCsv(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream().map(String::trim).filter(v -> !v.isBlank()).collect(Collectors.joining(","));
    }

    public List<UUID> parseUuidCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .map(UUID::fromString)
                .toList();
    }

    public String joinUuidCsv(List<UUID> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream().map(UUID::toString).collect(Collectors.joining(","));
    }
}
