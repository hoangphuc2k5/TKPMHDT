package TKPMHDT.Controller.api.quantrivien;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.HoaDon;
import TKPMHDT.Entity.khuyenmai.KhuyenMaiGiaSanPham;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.khuyenmai.enums.PhamViKhuyenMaiGia;
import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.sanpham.CongThuc;
import TKPMHDT.Entity.sanpham.LuongNguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.SanPham;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminPayloadMapper {

    private final NuocUongSanRepository nuocUongSanRepository;

    public Map<String, Object> toUserPayload(NguoiDung user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getId());
        payload.put("tenDangNhap", user.getTenDangNhap());
        payload.put("email", user.getEmail());
        payload.put("vaiTro", user.getVaiTro());
        payload.put("active", user.isTrangThaiHoatDong());
        payload.put("trangThaiHoatDong", user.isTrangThaiHoatDong());
        if (user instanceof KhachHang kh) {
            payload.put("hoTen", kh.getHoTen());
            payload.put("soDienThoai", kh.getSoDienThoai());
        }
        return payload;
    }

    public boolean checkUserMatch(NguoiDung u, String keyword) {
        String k = keyword.toLowerCase(Locale.ROOT);
        String username = u.getTenDangNhap() != null ? u.getTenDangNhap().toLowerCase(Locale.ROOT) : "";
        String email = u.getEmail() != null ? u.getEmail().toLowerCase(Locale.ROOT) : "";
        String hoten = "";
        if (u instanceof KhachHang kh && kh.getHoTen() != null) {
            hoten = kh.getHoTen().toLowerCase(Locale.ROOT);
        }
        return username.contains(k) || email.contains(k) || hoten.contains(k);
    }

    public String mapTrangThaiVeDb(String input) {
        if (input == null) {
            return "CHO_XAC_NHAN";
        }
        String normalized = input.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CHO_XU_LY", "CHO_XAC_NHAN" -> "CHO_XAC_NHAN";
            case "DANG_LAM", "DA_XAC_NHAN", "DANG_CHUAN_BI" -> "DA_XAC_NHAN";
            case "DANG_GIAO", "GIAO_HANG" -> "DANG_GIAO";
            case "HOAN_THANH", "DA_GIAO" -> "DA_GIAO";
            case "HUY", "DA_HUY" -> "DA_HUY";
            default -> normalized;
        };
    }

    public Map<String, Object> toDiaChiPayload(DiaChi diaChi) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", diaChi.getId());
        payload.put("tenNguoiNhan", diaChi.getTenNguoiNhan());
        payload.put("soDienThoai", diaChi.getSoDienThoai());
        payload.put("diaChiCuThe", diaChi.getDiaChiCuThe());
        payload.put("phuongXa", diaChi.getPhuongXa());
        payload.put("quanHuyen", diaChi.getQuanHuyen());
        payload.put("tinhThanhPho", diaChi.getTinhThanhPho());
        payload.put("laMacDinh", diaChi.isLaMacDinh());
        return payload;
    }

    public Map<String, Object> toDonHangDetailPayload(DonHang donHang) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", donHang.getId());
        payload.put("ngayDat", donHang.getNgayDat());
        payload.put("tongTien", donHang.getTongTien());
        payload.put("tienGiamApDung", donHang.getTienGiamApDung());
        payload.put("trangThaiDb", donHang.getTrangThaiDb());
        payload.put("trangThai", donHang.getTrangThaiCode());
        KhachHang kh = donHang.getKhachHang();
        String tenKhach = "Khách lẻ";
        if (kh != null) {
            if (kh.getHoTen() != null && !kh.getHoTen().isBlank()) {
                tenKhach = kh.getHoTen();
            } else if (kh.getTenDangNhap() != null) {
                tenKhach = kh.getTenDangNhap();
            }
        }
        payload.put("khachHang", kh != null && kh.getTenDangNhap() != null ? kh.getTenDangNhap() : "Khách lẻ");
        payload.put("tenKhachHang", tenKhach);
        ThanhToan tt = donHang.getThanhToan();
        if (tt != null) {
            if (tt.getPhuongThuc() != null) {
                payload.put("phuongThucThanhToan", tt.getPhuongThuc().name());
            }
            if (tt.getTrangThai() != null) {
                payload.put("trangThaiThanhToan", tt.getTrangThai().name());
            }
        }
        if (donHang.getDiaChiGiaoHang() != null) {
            payload.put("diaChiGiaoHang", toDiaChiPayload(donHang.getDiaChiGiaoHang()));
        }
        if (donHang.getMaGiamGia() != null) {
            payload.put("maGiamGia", donHang.getMaGiamGia().getMa());
        }
        List<ChiTietDonHang> lines = donHang.getChiTietDonHangs();
        List<Map<String, Object>> chiTietRows =
                lines == null ? List.of() : lines.stream().map(this::toChiTietDonHangAdminPayload).toList();
        payload.put("chiTiet", chiTietRows);
        payload.put("chiTietDonHang", chiTietRows);
        return payload;
    }

    private Map<String, Object> toCongThucSanPhamPayload(NuocUongSan sp) {
        if (sp == null || sp.getCongThucCoBan() == null) {
            return null;
        }
        CongThuc ct = sp.getCongThucCoBan();
        List<Map<String, Object>> ngl = new ArrayList<>();
        if (ct.getLuongNguyenLieus() != null) {
            for (LuongNguyenLieu ll : ct.getLuongNguyenLieus()) {
                if (ll.getNguyenLieu() == null) {
                    continue;
                }
                Map<String, Object> row = new HashMap<>();
                row.put("tenNguyenLieu", ll.getNguyenLieu().getTen());
                row.put("soLuong", ll.getSoLuong());
                row.put("donVi", ll.getDonVi());
                ngl.add(row);
            }
        }
        Map<String, Object> m = new HashMap<>();
        m.put("tenCongThuc", ct.getTen());
        m.put("giaCoBan", ct.getGiaCoBan());
        m.put("moTa", ct.getMoTa());
        m.put("nguyenLieus", ngl);
        return m;
    }

    private Map<String, Object> toChiTietDonHangAdminPayload(ChiTietDonHang ct) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ct.getId());
        NuocUongSan sp = ct.getNuocUong();
        String tenSp = sp != null && sp.getTen() != null ? sp.getTen() : "";
        m.put("sanPham", tenSp);
        m.put("tenSanPham", tenSp);
        m.put("soLuong", ct.getSoLuong());
        m.put("thanhTien", ct.getThanhTien());
        m.put("giaTien", sp != null ? sp.getGia() : null);
        m.put("congThuc", toCongThucSanPhamPayload(sp));
        m.put("tuyChinh", toTuyChinhKhachHangPayload(ct.getTuyChinh()));
        if (ct.getTuyChinh() != null && ct.getTuyChinh().getMucDa() != null) {
            m.put("mucDa", ct.getTuyChinh().getMucDa());
        }
        if (ct.getTuyChinh() != null && ct.getTuyChinh().getGhiChu() != null) {
            m.put("ghiChu", ct.getTuyChinh().getGhiChu());
        }
        if (ct.getToppings() != null && !ct.getToppings().isEmpty()) {
            m.put("toppings", ct.getToppings().stream().map(t -> {
                Map<String, Object> tm = new HashMap<>();
                tm.put("ten", t.getNguyenLieu() != null ? t.getNguyenLieu().getTen() : t.getTen());
                tm.put("soLuong", t.getSoLuong());
                tm.put("donGia", t.getDonGia());
                tm.put("giaTien", t.getDonGia());
                return tm;
            }).toList());
        }
        return m;
    }

    private Map<String, Object> toTuyChinhKhachHangPayload(TuyChinhKhachHang tc) {
        Map<String, Object> m = new HashMap<>();
        if (tc == null) {
            return m;
        }
        m.put("mucDa", tc.getMucDa());
        m.put("ghiChu", tc.getGhiChu());
        return m;
    }

    public Map<String, Object> toHoaDonPayload(HoaDon hoaDon) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", hoaDon.getId());
        payload.put("soHoaDon", hoaDon.getSoHoaDon());
        payload.put("ngayLap", hoaDon.getNgayLap());
        payload.put("tongTien", hoaDon.getTongTien());
        payload.put("tienGiam", hoaDon.getTienGiam());
        payload.put("tienThanhToan", hoaDon.getTienThanhToan());
        payload.put("trangThaiHoaDon", hoaDon.getTrangThaiHoaDon());
        payload.put("donHangId", hoaDon.getDonHang() != null ? hoaDon.getDonHang().getId() : null);
        return payload;
    }

    public Map<String, Object> toHoaDonDetailPayload(HoaDon hoaDon) {
        Map<String, Object> payload = toHoaDonPayload(hoaDon);
        payload.put("phuongThucThanhToan", hoaDon.getPhuongThucThanhToan());
        payload.put("ghiChu", hoaDon.getGhiChu());
        payload.put("ngayIn", hoaDon.getNgayIn());
        if (hoaDon.getDonHang() != null) {
            payload.put("donHang", toDonHangDetailPayload(hoaDon.getDonHang()));
        }
        return payload;
    }

    public static LinkedHashSet<String> chuanHoaDanhMuc(List<String> raw) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        if (raw == null) {
            return out;
        }
        for (String s : raw) {
            if (s == null) {
                continue;
            }
            String t = s.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }

    public Map<String, Object> toKhuyenMaiPayload(MaGiamGia entity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", entity.getId());
        payload.put("ma", entity.getMa());
        payload.put("loaiGiam", entity.getLoaiGiam());
        payload.put("giaTri", entity.getGiaTri());
        payload.put("ngayBatDau", entity.getNgayBatDau());
        payload.put("ngayKetThuc", entity.getNgayKetThuc());
        payload.put("kichHoat", entity.isKichHoat());
        payload.put("apDungToanHeThong", entity.isApDungToanHeThong());
        payload.put("sanPhamIds", entity.getSanPhamApDung() == null ? List.of()
                : entity.getSanPhamApDung().stream().map(SanPham::getId).toList());
        payload.put("cacNgayApDung", entity.getCacNgayApDung() == null ? List.of() : new ArrayList<>(entity.getCacNgayApDung()));
        payload.put("danhMucApDung", entity.getDanhMucApDung() == null ? List.of() : new ArrayList<>(entity.getDanhMucApDung()));
        return payload;
    }

    public void ganPhamViKhuyenMaiGia(KhuyenMaiGiaSanPham entity, KhuyenMaiGiaSanPhamRequest request) {
        PhamViKhuyenMaiGia pv = request.phamVi() != null ? request.phamVi() : entity.getPhamVi();
        if (pv == null) {
            throw new IllegalArgumentException("Thiếu phạm vi áp dụng");
        }
        entity.setPhamVi(pv);
        entity.setSanPhamDon(null);
        entity.setDanhMuc(null);
        if (entity.getSanPhams() == null) {
            entity.setSanPhams(new HashSet<>());
        } else {
            entity.getSanPhams().clear();
        }
        switch (pv) {
            case MOT_SAN_PHAM -> {
                UUID sid = request.sanPhamDonId();
                if (sid == null) {
                    throw new IllegalArgumentException("Chọn một sản phẩm");
                }
                entity.setSanPhamDon(nuocUongSanRepository
                        .findById(sid)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm")));
            }
            case NHIEU_SAN_PHAM -> {
                List<UUID> ids = request.sanPhamIds();
                if (ids == null || ids.isEmpty()) {
                    throw new IllegalArgumentException("Chọn ít nhất một sản phẩm");
                }
                entity.getSanPhams().addAll(new HashSet<>(nuocUongSanRepository.findAllById(ids)));
            }
            case DANH_MUC -> {
                String dm = request.danhMuc();
                if (dm == null || dm.isBlank()) {
                    throw new IllegalArgumentException("Nhập tên danh mục áp dụng");
                }
                entity.setDanhMuc(dm.trim());
            }
        }
    }

    public Map<String, Object> toKhuyenMaiGiaPayload(KhuyenMaiGiaSanPham e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.getId());
        m.put("ten", e.getTen());
        m.put("phamVi", e.getPhamVi());
        m.put("loaiGiam", e.getLoaiGiam());
        m.put("giaTri", e.getGiaTri());
        m.put("thoiGianBatDau", e.getThoiGianBatDau());
        m.put("thoiGianKetThuc", e.getThoiGianKetThuc());
        m.put("kichHoat", e.isKichHoat());
        m.put("sanPhamDonId", e.getSanPhamDon() != null ? e.getSanPhamDon().getId() : null);
        m.put(
                "sanPhamIds",
                e.getSanPhams() == null
                        ? List.of()
                        : e.getSanPhams().stream().map(SanPham::getId).toList());
        m.put("danhMuc", e.getDanhMuc());
        return m;
    }
}
