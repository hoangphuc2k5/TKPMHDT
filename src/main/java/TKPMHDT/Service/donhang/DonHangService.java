package TKPMHDT.Service.donhang;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TKPMHDT.DTO.request.TinhTienGiamGioHangRequest;
import TKPMHDT.DTO.request.SanPhamOrderRequest;
import TKPMHDT.DTO.request.TaoThanhToanRequest;
import TKPMHDT.DTO.request.ToppingRequest;
import TKPMHDT.DTO.request.TuyChinhRequest;
import TKPMHDT.DTO.response.ChiTietDonHangResponse;
import TKPMHDT.DTO.response.CongThucResponse;
import TKPMHDT.DTO.response.DonHangResponse;
import TKPMHDT.DTO.response.NguyenLieuTrongCongThucResponse;
import TKPMHDT.DTO.response.ToppingResponse;
import TKPMHDT.DTO.response.XemDonHangResponse;
import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiChoXacNhan;
import TKPMHDT.Entity.giohang.ChiTietGioHang;
import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.sanpham.ChiTietTopping;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import TKPMHDT.Repository.donhang.ChiTietDonHangRepository;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.giohang.GioHangRepository;
import TKPMHDT.Repository.nguoidung.DiaChiRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Service.giohang.GioHangService;
import TKPMHDT.Service.khuyenmai.KhuyenMaiGiaSanPhamService;
import TKPMHDT.Service.khuyenmai.KhuyenMaiService;
import TKPMHDT.Service.thanhtoan.ThanhToanService;

@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final GioHangRepository gioHangRepository;
    private final KhuyenMaiService khuyenMaiService;
    private final KhachHangRepository khachHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DiaChiRepository diaChiRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final ThanhToanService thanhToanService;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final DonHangRealtimeService donHangRealtimeService;
    private final KhuyenMaiGiaSanPhamService khuyenMaiGiaSanPhamService;
    private final GioHangService gioHangService;

    public DonHangService(
            DonHangRepository donHangRepository,
            GioHangRepository gioHangRepository,
            KhuyenMaiService khuyenMaiService,
            KhachHangRepository khachHangRepository,
            NguoiDungRepository nguoiDungRepository,
            DiaChiRepository diaChiRepository,
            NuocUongSanRepository nuocUongSanRepository,
            ThanhToanService thanhToanService,
            ChiTietDonHangRepository chiTietDonHangRepository,
            NguyenLieuRepository nguyenLieuRepository,
            DonHangRealtimeService donHangRealtimeService,
            KhuyenMaiGiaSanPhamService khuyenMaiGiaSanPhamService,
            GioHangService gioHangService
    ) {
        this.donHangRepository = donHangRepository;
        this.gioHangRepository = gioHangRepository;
        this.khuyenMaiService = khuyenMaiService;
        this.khachHangRepository = khachHangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.diaChiRepository = diaChiRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.thanhToanService = thanhToanService;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.donHangRealtimeService = donHangRealtimeService;
        this.khuyenMaiGiaSanPhamService = khuyenMaiGiaSanPhamService;
        this.gioHangService = gioHangService;
    }

    @Transactional
    public DonHang taoDonHangTuGioHang(UUID khachHangId, UUID diaChiId, String maGiamGiaCode, PhuongThucThanhToanEnum phuongThuc) {
        GioHang gioHang = gioHangService.layVaDongBoGiaTheoKhuyenMai(khachHangId);

        List<ChiTietGioHang> matHangDuocChon = gioHang.getCacMatHang().stream()
                .filter(ChiTietGioHang::isDuocChonThanhToan)
                .toList();

        if (matHangDuocChon.isEmpty()) {
            throw new IllegalStateException("Không có mặt hàng nào được chọn để thanh toán");
        }

        DiaChi diaChi = resolveDiaChiGiaoHang(khachHangId, diaChiId);

        LocalDate ngayApDung = LocalDate.now();
        Optional<MaGiamGia> maGiamGiaOpt = Optional.empty();
        BigDecimal tienGiam = BigDecimal.ZERO;
        if (maGiamGiaCode != null && !maGiamGiaCode.isBlank()) {
            MaGiamGia mg = khuyenMaiService
                    .timMaHopLe(maGiamGiaCode.trim(), ngayApDung)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mã giảm giá không tồn tại, đã tắt hoặc không nằm trong thời gian áp dụng."));
            BigDecimal coSo = khuyenMaiService.tongTienHangDuocApDung(mg, matHangDuocChon);
            if (coSo.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Mã giảm giá không áp dụng cho các sản phẩm đang chọn.");
            }
            tienGiam = khuyenMaiService.tinhTienGiam(mg, coSo);
            maGiamGiaOpt = Optional.of(mg);
        }

        BigDecimal tongTienGoc = matHangDuocChon.stream()
                .map(ChiTietGioHang::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tongThanhToan = tongTienGoc.subtract(tienGiam).max(BigDecimal.ZERO);

        DonHang donHang = DonHang.builder()
                .khachHang(gioHang.getKhachHang())
                .ngayDat(LocalDateTime.now())
                .trangThai(new TrangThaiChoXacNhan()) // State Pattern
                .tongTien(tongThanhToan)
                .tienGiamApDung(tienGiam)
                .maGiamGia(maGiamGiaOpt.orElse(null))
                .diaChiGiaoHang(diaChi)
                .chiTietDonHangs(new ArrayList<>())
                .build();

        List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();
        for (ChiTietGioHang item : matHangDuocChon) {
            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .donHang(donHang)
                    .soLuong(item.getSoLuong())
                    .nuocUong(item.getNuocUong())
                    .tuyChinh(item.getTuyChinh())
                    .thanhTien(item.getThanhTien())
                    .toppings(new ArrayList<>())
                    .build();
            if (item.getToppings() != null) {
                for (ChiTietTopping src : item.getToppings()) {
                    ChiTietTopping copy = ChiTietTopping.builder()
                            .nguyenLieu(src.getNguyenLieu())
                            .soLuong(src.getSoLuong())
                            .donGia(src.getDonGia())
                            .ten(src.getTen())
                            .chiTietDonHang(chiTiet)
                            .build();
                    chiTiet.getToppings().add(copy);
                }
            }
            chiTietDonHangs.add(chiTiet);
        }
        donHang.setChiTietDonHangs(chiTietDonHangs);

        DonHang saved = donHangRepository.save(donHang);
        TaoThanhToanRequest thanhToanRequest = new TaoThanhToanRequest();
        thanhToanRequest.setDonHangId(saved.getId());
        thanhToanRequest.setPhuongThuc(phuongThuc);
        thanhToanService.taoThanhToan(thanhToanRequest);
        donHangRealtimeService.publishOrderUpdated(saved);

        gioHang.getCacMatHang().removeIf(ChiTietGioHang::isDuocChonThanhToan);
        gioHang.setTongTien(gioHang.getCacMatHang().stream()
                .map(ChiTietGioHang::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        gioHangRepository.save(gioHang);

        return saved;
    }

    @Transactional
    public DonHang taoDonHangTuSanPham(UUID khachHangId,
                                       UUID nuocUongId,
                                       int soLuong,
                                       Integer mucDa,
                                       String ghiChu,
                                       UUID diaChiId,
                                       String maGiamGiaCode,
                                       PhuongThucThanhToanEnum phuongThuc) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));

        DiaChi diaChi = resolveDiaChiGiaoHang(khachHangId, diaChiId);

        NuocUongSan nuocUong = nuocUongSanRepository.findById(nuocUongId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        BigDecimal donGiaSauKm = khuyenMaiGiaSanPhamService.donGiaCoSoSauKhuyenMai(nuocUong);
        BigDecimal tongTienGoc = donGiaSauKm.multiply(BigDecimal.valueOf(soLuong));

        LocalDate ngayApDung = LocalDate.now();
        Optional<MaGiamGia> maGiamGiaOpt = Optional.empty();
        BigDecimal tienGiam = BigDecimal.ZERO;
        if (maGiamGiaCode != null && !maGiamGiaCode.isBlank()) {
            MaGiamGia mg = khuyenMaiService
                    .timMaHopLe(maGiamGiaCode.trim(), ngayApDung)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mã giảm giá không tồn tại, đã tắt hoặc không nằm trong thời gian áp dụng."));
            BigDecimal coSo = khuyenMaiService.nuocUongDuocApDung(mg, nuocUong)
                    ? tongTienGoc
                    : BigDecimal.ZERO;
            if (coSo.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Mã giảm giá không áp dụng cho sản phẩm này.");
            }
            tienGiam = khuyenMaiService.tinhTienGiam(mg, coSo);
            maGiamGiaOpt = Optional.of(mg);
        }
        BigDecimal tongThanhToan = tongTienGoc.subtract(tienGiam).max(BigDecimal.ZERO);

        DonHang donHang = DonHang.builder()
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now())
                .trangThai(new TrangThaiChoXacNhan())
                .tongTien(tongThanhToan)
                .tienGiamApDung(tienGiam)
                .maGiamGia(maGiamGiaOpt.orElse(null))
                .diaChiGiaoHang(diaChi)
                .chiTietDonHangs(new ArrayList<>())
                .build();

        ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                .donHang(donHang)
                .soLuong(soLuong)
                .nuocUong(nuocUong)
                .tuyChinh(TuyChinhKhachHang.builder()                        
                        .mucDa(mucDa)
                        .ghiChu(ghiChu)
                        .build())
                .thanhTien(tongTienGoc)
                .build();

        donHang.getChiTietDonHangs().add(chiTiet);

        DonHang saved = donHangRepository.save(donHang);
        TaoThanhToanRequest thanhToanRequest = new TaoThanhToanRequest();
        thanhToanRequest.setDonHangId(saved.getId());
        thanhToanRequest.setPhuongThuc(phuongThuc);
        thanhToanService.taoThanhToan(thanhToanRequest);
        donHangRealtimeService.publishOrderUpdated(saved);
        return saved;
    }

    @Transactional
    public DonHang xacNhanDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.xacNhan();
        DonHang saved = donHangRepository.save(donHang);
        donHangRealtimeService.publishOrderUpdated(saved);
        return saved;
    }

    @Transactional
    public DonHang giaoDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.giaoHang();
        DonHang saved = donHangRepository.save(donHang);
        donHangRealtimeService.publishOrderUpdated(saved);
        return saved;
    }

    @Transactional
    public DonHang hoanThanhDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.hoanThanh();
        DonHang saved = donHangRepository.save(donHang);
        donHangRealtimeService.publishOrderUpdated(saved);
        return saved;
    }

    @Transactional
    public DonHang huyDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.huyDon();
        DonHang saved = donHangRepository.save(donHang);
        donHangRealtimeService.publishOrderUpdated(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<DonHang> layDonHangTheoKhachHang(UUID khachHangId) {
        return donHangRepository.findByKhachHangId(khachHangId);
    }

    @Transactional(readOnly = true)
    public Optional<DonHang> layTheoId(UUID donHangId) {
        return donHangRepository.findById(donHangId);
    }

    @Transactional(readOnly = true)
    // lấy đơn hàng trả về XemDonHangResponse để hiển thị cho khách hàng
    public XemDonHangResponse layChiTietDonHang(UUID donHangId) {
        
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        // Xây chi tiết đơn hàng response
        XemDonHangResponse response = mapToXemDonHangResponse(donHang);
        return response;
    }

    private XemDonHangResponse mapToXemDonHangResponse(DonHang donHang) {
        BigDecimal tamTinhHang = donHang.getChiTietDonHangs() == null
                ? BigDecimal.ZERO
                : donHang.getChiTietDonHangs().stream()
                        .map(ChiTietDonHang::getThanhTien)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tienGiam = donHang.getTienGiamApDung() != null ? donHang.getTienGiamApDung() : BigDecimal.ZERO;

        List<ChiTietDonHangResponse> chiTietResponses = donHang.getChiTietDonHangs()
            .stream()
            .map(ct -> {
                List<ChiTietTopping> chiTietToppings = ct.getToppings() != null ? ct.getToppings() : new ArrayList<>();
                List<ToppingResponse> toppingRes = chiTietToppings.stream()
                        .map(t -> ToppingResponse.builder()
                                .ten(t.getTen() != null && !t.getTen().isBlank()
                                        ? t.getTen()
                                        : (t.getNguyenLieu() != null ? t.getNguyenLieu().getTen() : "Topping"))
                                .soLuong(t.getSoLuong() != null ? t.getSoLuong() : 1)
                                .giaTien(t.getDonGia())
                                .build())
                        .toList();

                TuyChinhKhachHang tc = ct.getTuyChinh();
                return ChiTietDonHangResponse.builder()
                        .idChiTietDonHang(ct.getId())
                        .tenSanPham(ct.getNuocUong().getTen())
                        .giaTien(ct.getNuocUong().getGia())
                        .mucDa(tc != null ? tc.getMucDa() : null)
                        .ghiChu(tc != null ? tc.getGhiChu() : null)
                        .soLuong(ct.getSoLuong())
                        .thanhTien(ct.getThanhTien())
                        .toppings(toppingRes)
                        .congThuc(mapToCongThucResponse(ct.getNuocUong()))
                        .build();
            })
            .toList();
        
        XemDonHangResponse response = XemDonHangResponse.builder()
                .idDonHang(donHang.getId())
                .trangThai(donHang.getTrangThaiCode())
                .ngayDat(donHang.getNgayDat())
                .tenKhachHang(donHang.getKhachHang() != null ? (donHang.getKhachHang().getHoTen() != null ? donHang.getKhachHang().getTenDangNhap() : donHang.getKhachHang().getHoTen()) : "Khách vãng lai")
                .chiTietDonHang(chiTietResponses)
                .diaChiGiaoHang(donHang.getDiaChiGiaoHang())
                .phuongThucThanhToan(donHang.getThanhToan() != null ? donHang.getThanhToan().getPhuongThuc().name() : "Chưa xác định")
                .trangThaiThanhToan(donHang.getThanhToan() != null ? donHang.getThanhToan().getTrangThai().name() : "Chưa xác định")      
                .tamTinhHang(tamTinhHang)
                .tienGiamApDung(tienGiam)
                .maGiamGia(donHang.getMaGiamGia() != null ? donHang.getMaGiamGia().getMa() : null)
                .tongTien(donHang.getTongTien())      
                .build();
        return response;
    }

    private CongThucResponse mapToCongThucResponse(NuocUongSan sanPham) {
        if (sanPham == null || sanPham.getCongThucCoBan() == null) {
            return null;
        }

        List<NguyenLieuTrongCongThucResponse> nguyenLieuResponses = sanPham.getCongThucCoBan().getLuongNguyenLieus()
                .stream()
                .map(ll -> NguyenLieuTrongCongThucResponse.builder()
                        .tenNguyenLieu(ll.getNguyenLieu().getTen())
                        .soLuong(ll.getSoLuong())
                        .donVi(ll.getDonVi())
                        .build())
                .toList();

        return CongThucResponse.builder()
                .tenCongThuc(sanPham.getCongThucCoBan().getTen())
                .giaCoBan(sanPham.getCongThucCoBan().getGiaCoBan())
                .moTa(sanPham.getCongThucCoBan().getMoTa())
                .nguyenLieus(nguyenLieuResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public List<DonHang> layDonHangCuaToi(String tenDangNhap) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan"));
        return donHangRepository.findByKhachHangId(nguoiDung.getId());
    }

    @Transactional(readOnly = true)
    public List<DonHang> layTatCaDonHang() {
        return donHangRepository.findAll();
    }

    //Lấy tất cả đơn hàng OFFLINE
    public Page<DonHangResponse> layTatCaDonHangOffline(Pageable pageable) {
        return donHangRepository.findByKhachHangIsNull(pageable)
                .map(this::mapToDonHangResponse);
    }
    
    public DonHangResponse layDonHangTaiQuay(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        return mapToDonHangResponse(donHang);
    }

    // B1: Tạo đơn hàng mới với trạng thái "CHO_XAC_NHAN"
    @Transactional
    public UUID taoDonHangTaiQuay() {
        DonHang donHang = DonHang.builder()
                .khachHang(null) // POS orders may not have a customer
                .ngayDat(LocalDateTime.now())
                .trangThai(new TrangThaiChoXacNhan())
                .tongTien(BigDecimal.ZERO)
                .tienGiamApDung(BigDecimal.ZERO)
                .chiTietDonHangs(new ArrayList<>())
                .build();

        donHangRepository.save(donHang);
        return donHang.getId();
    }
    // B2: Thêm chi tiết sản phẩm vào đơn hàng
    @Transactional
    public DonHangResponse themChiTietVaoDonHangTaiQuay(UUID donHangId, SanPhamOrderRequest sanPhamRequest) {
        DonHang donHang = donHangRepository.findById(donHangId).
                orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        
        // Kiểm tra trạng thái đơn hàng
        if (!"CHO_XAC_NHAN".equalsIgnoreCase(donHang.getTrangThaiCode())) {
            throw new IllegalStateException("Chỉ có thể thêm sản phẩm vào đơn hàng đang ở trạng thái CHO_XAC_NHAN");
        }
        
        NuocUongSan nuocUongSan = nuocUongSanRepository.findById(sanPhamRequest.getNuocUongId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        
        TuyChinhRequest req = sanPhamRequest.getTuyChinh();
        TuyChinhKhachHang tuyChinh = TuyChinhKhachHang.builder()
                .mucDa(req != null ? req.getMucDa() : null)
                .ghiChu(req != null ? req.getGhiChu() : null)
                .build();

        // Xây topping
        List<ChiTietTopping> chiTietToppings = new ArrayList<>();
        List<ToppingRequest> toppingRequests = sanPhamRequest.getToppings() != null ? sanPhamRequest.getToppings() : new ArrayList<>();
        for (ToppingRequest toppingReq : toppingRequests) {
            if (toppingReq == null) {
                continue;
            }
            if (toppingReq.getNguyenLieuId() == null && toppingReq.getToppingId() == null && toppingReq.getGiaThem() == null) {
                continue;
            }

            NguyenLieu nguyenLieu = null;
            BigDecimal price = toppingReq.getGiaThem();
            BigDecimal defaultPrice = price;
            String ten = null;
            int quantity = toppingReq.getSoLuong() == null || toppingReq.getSoLuong() <= 0 ? 1 : toppingReq.getSoLuong();

            if (toppingReq.getNguyenLieuId() != null) {
                nguyenLieu = nguyenLieuRepository.findById(toppingReq.getNguyenLieuId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu topping với ID: " + toppingReq.getNguyenLieuId()));
                damBaoLaTopping(nguyenLieu);
                price = nguyenLieu.getGiaDonVi() != null ? nguyenLieu.getGiaDonVi() : price;
                ten = nguyenLieu.getTen();
            } else if (toppingReq.getToppingId() != null) {
                nguyenLieu = nguyenLieuRepository.findById(toppingReq.getToppingId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu topping (toppingId): " + toppingReq.getToppingId()));
                damBaoLaTopping(nguyenLieu);
                price = nguyenLieu.getGiaDonVi() != null ? nguyenLieu.getGiaDonVi() : defaultPrice;
                ten = nguyenLieu.getTen();
            } else if (toppingReq.getGiaThem() != null) {
                price = toppingReq.getGiaThem();
                ten = toppingReq.getTen();
            }

            if (price == null) {
                price = BigDecimal.ZERO;
            }
            if (ten == null) {
                ten = "Topping";
            }

            ChiTietTopping chiTietTopping = ChiTietTopping.builder()
                    .nguyenLieu(nguyenLieu)
                    .soLuong(quantity)
                    .donGia(price)
                    .ten(ten)
                    .build();

            chiTietToppings.add(chiTietTopping);
        }


        // Tính lại giá cuối cùng sau khi có tùy chỉnh và topping
        BigDecimal giaCoBan = khuyenMaiGiaSanPhamService.donGiaCoSoSauKhuyenMai(nuocUongSan);
        BigDecimal giaCuoiCung = tuyChinh.tinhGiaCuoiCung(giaCoBan);
        for (ChiTietTopping topping : chiTietToppings) {
            giaCuoiCung = giaCuoiCung.add(topping.getDonGia().multiply(BigDecimal.valueOf(topping.getSoLuong())));
        }

        // Kiểm tra xem đã có chi tiết đơn hàng nào giống hệt (cùng sản phẩm, cùng tùy chỉnh, cùng topping) chưa
        for (ChiTietDonHang existingChiTiet : donHang.getChiTietDonHangs()) {
            if (KiemTraChiTietDonHangGiongNhau(existingChiTiet, ChiTietDonHang.builder()
                    .nuocUong(nuocUongSan)
                    .tuyChinh(tuyChinh)
                    .toppings(chiTietToppings)
                    .build())) {
                // Nếu có, cập nhật số lượng và thành tiền
                existingChiTiet.setSoLuong(existingChiTiet.getSoLuong() + sanPhamRequest.getSoLuong());
                existingChiTiet.setThanhTien(existingChiTiet.getThanhTien().add(giaCuoiCung.multiply(BigDecimal.valueOf(sanPhamRequest.getSoLuong()))));
                
                // Cập nhật tổng tiền của đơn hàng
                //donHang.setTongTien(donHang.getTongTien().add(giaCuoiCung.multiply(BigDecimal.valueOf(sanPhamRequest.getSoLuong()))));
                recalcTongTien(donHang);
                
                donHangRepository.save(donHang);
                return mapToDonHangResponse(donHang);
            }
        }
        
        // Xây chi tiết đơn hàng mới
        ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                .donHang(donHang)
                .soLuong(sanPhamRequest.getSoLuong()) // Mặc định 1 sản phẩm, có thể mở rộng sau
                .nuocUong(nuocUongSan)
                .tuyChinh(tuyChinh) // Sử dụng tùy chỉnh đã xây dựng
                .toppings(chiTietToppings) // Gán danh sách topping đã xây dựng
                .thanhTien(giaCuoiCung.multiply(BigDecimal.valueOf(sanPhamRequest.getSoLuong()))) // Giá bán của sản phẩm
                .build();

        donHang.getChiTietDonHangs().add(chiTiet);

        // Thiết lập quan hệ hai chiều cho toppings
        for (ChiTietTopping topping : chiTietToppings) {
            topping.setChiTietDonHang(chiTiet);
        }

        // Cập nhật tổng tiền của đơn hàng
        // BigDecimal tongTien = donHang.getTongTien().add(chiTiet.getThanhTien());
        // donHang.setTongTien(tongTien);

        recalcTongTien(donHang);

        // Lưu đơn hàng với chi tiết mới
        donHangRepository.save(donHang);

        return mapToDonHangResponse(donHang);
    }

    /**
     * Cộng thành tiền từng dòng; nếu có mã voucher thì tính lại {@code tienGiamApDung} và
     * {@code tongTien} = tạm tính − giảm. Nếu mã không còn áp dụng (đổi giỏ) thì gỡ mã.
     */
    private void recalcTongTien(DonHang donHang) {
        BigDecimal tongHang = donHang.getChiTietDonHangs()
                .stream()
                .map(ChiTietDonHang::getThanhTien)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MaGiamGia mg = donHang.getMaGiamGia();
        if (mg != null && donHang.getChiTietDonHangs() != null && !donHang.getChiTietDonHangs().isEmpty()) {
            List<TinhTienGiamGioHangRequest.DongGioTinhGiam> dong = donHang.getChiTietDonHangs().stream()
                    .map(ct -> new TinhTienGiamGioHangRequest.DongGioTinhGiam(
                            ct.getNuocUong().getId(),
                            ct.getThanhTien()))
                    .toList();
            BigDecimal coSo = khuyenMaiService.tongTienDuocApDungTuCacDong(mg, dong);
            if (coSo.compareTo(BigDecimal.ZERO) <= 0) {
                donHang.setMaGiamGia(null);
                donHang.setTienGiamApDung(BigDecimal.ZERO);
                donHang.setTongTien(tongHang);
                return;
            }
            BigDecimal tienGiam = khuyenMaiService.tinhTienGiam(mg, coSo);
            donHang.setTienGiamApDung(tienGiam);
            donHang.setTongTien(tongHang.subtract(tienGiam).max(BigDecimal.ZERO));
            return;
        }
        if (donHang.getChiTietDonHangs() == null || donHang.getChiTietDonHangs().isEmpty()) {
            donHang.setMaGiamGia(null);
        }
        donHang.setTienGiamApDung(BigDecimal.ZERO);
        donHang.setTongTien(tongHang);
    }

    /**
     * Áp dụng hoặc gỡ mã giảm giá (voucher) cho đơn tại quầy đang {@code CHO_XAC_NHAN}.
     *
     * @param ma mã hợp lệ, hoặc {@code null}/rỗng để gỡ mã
     */
    @Transactional
    public DonHangResponse apDungMaGiamGiaChoDonTaiQuay(UUID donHangId, String ma) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (!"CHO_XAC_NHAN".equalsIgnoreCase(donHang.getTrangThaiCode())) {
            throw new IllegalArgumentException("Chỉ áp dụng mã khi đơn đang chờ xác nhận");
        }
        if (ma == null || ma.isBlank()) {
            donHang.setMaGiamGia(null);
            donHang.setTienGiamApDung(BigDecimal.ZERO);
            recalcTongTien(donHang);
            donHangRepository.save(donHang);
            return mapToDonHangResponse(donHang);
        }
        if (donHang.getChiTietDonHangs() == null || donHang.getChiTietDonHangs().isEmpty()) {
            throw new IllegalArgumentException("Thêm sản phẩm vào đơn trước khi nhập mã");
        }
        MaGiamGia mg = khuyenMaiService
                .timMaHopLe(ma.trim(), LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("Mã không tồn tại hoặc không còn hiệu lực"));
        List<TinhTienGiamGioHangRequest.DongGioTinhGiam> dong = donHang.getChiTietDonHangs().stream()
                .map(ct -> new TinhTienGiamGioHangRequest.DongGioTinhGiam(
                        ct.getNuocUong().getId(),
                        ct.getThanhTien()))
                .toList();
        BigDecimal coSo = khuyenMaiService.tongTienDuocApDungTuCacDong(mg, dong);
        if (coSo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Mã không áp dụng cho sản phẩm trong đơn");
        }
        donHang.setMaGiamGia(mg);
        recalcTongTien(donHang);
        donHangRepository.save(donHang);
        return mapToDonHangResponse(donHang);
    }

    // Tính lại thành tiền của chi tiết đơn hàng sau khi có sự thay đổi về số lượng hoặc topping
    private void recalcThanhTien(ChiTietDonHang chiTiet) {
        BigDecimal giaCoBan = khuyenMaiGiaSanPhamService.donGiaCoSoSauKhuyenMai(chiTiet.getNuocUong());
        BigDecimal giaCuoiCung = chiTiet.getTuyChinh().tinhGiaCuoiCung(giaCoBan);
        List<ChiTietTopping> chiTietToppings = chiTiet.getToppings() != null ? chiTiet.getToppings() : new ArrayList<>();
        for (ChiTietTopping topping : chiTietToppings) {
            BigDecimal toppingPrice = topping.getDonGia() != null ? topping.getDonGia() : BigDecimal.ZERO;
            int quantity = topping.getSoLuong() != null ? topping.getSoLuong() : 1;
            giaCuoiCung = giaCuoiCung.add(toppingPrice.multiply(BigDecimal.valueOf(quantity)));
        }
        chiTiet.setThanhTien(giaCuoiCung.multiply(BigDecimal.valueOf(chiTiet.getSoLuong())));
    }

    // B3: Khi khách hàng chọn sản phẩm xong, nhân viên có thể xác nhận đơn hàng, trạng thái chuyển thành "DA_XAC_NHAN", không cho phép thêm sản phẩm nữa,
    // Nhân viên chờ nhận tiền thanh toán rồi chuyển trạng thái thành "DANG_CHUAN_BI", sau đó là "DANG_GIAO", "DA_GIAO" tương tự như đơn hàng bình thường
    // Thanh toán
    @Transactional
    public void updateSoLuong(UUID chiTietId, int soLuongMoi) {

        ChiTietDonHang ct = chiTietDonHangRepository.findById(chiTietId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));

        DonHang donHang = ct.getDonHang();

        if (!"CHO_XAC_NHAN".equalsIgnoreCase(donHang.getTrangThaiCode())) {
            throw new IllegalStateException("Không được chỉnh sửa đơn");
        }

        if (soLuongMoi <= 0) {
            // xoá luôn
            donHang.getChiTietDonHangs().remove(ct);
            chiTietDonHangRepository.delete(ct);
        } else {

            // cập nhật số lượng
            ct.setSoLuong(soLuongMoi);

            recalcThanhTien(ct);
            chiTietDonHangRepository.save(ct);
        }

        // 🔥 tính lại tổng tiền
        recalcTongTien(donHang);
        donHangRepository.save(donHang);
    }

    public void xoaChiTietDonHang(UUID chiTietDonHangId) {
        updateSoLuong(chiTietDonHangId, 0);
    }

    public void tangSoLuong(UUID chiTietDonHangId) {
        ChiTietDonHang ct = chiTietDonHangRepository.findById(chiTietDonHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        int current = ct.getSoLuong();
        updateSoLuong(chiTietDonHangId, current + 1);
    }

    public void giamSoLuong(UUID chiTietDonHangId) {
        ChiTietDonHang ct = chiTietDonHangRepository.findById(chiTietDonHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        int current = ct.getSoLuong();
        updateSoLuong(chiTietDonHangId, current - 1);
    }
    

    // Map to DonHangResponse
    private DonHangResponse mapToDonHangResponse(DonHang donHang) {
        BigDecimal tamTinhHang = donHang.getChiTietDonHangs() == null
                ? BigDecimal.ZERO
                : donHang.getChiTietDonHangs().stream()
                        .map(ChiTietDonHang::getThanhTien)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ChiTietDonHangResponse> chiTietResponses = donHang.getChiTietDonHangs()
            .stream()
            .map(ct -> {
                // map toppings
                List<ChiTietTopping> chiTietToppings = ct.getToppings() != null ? ct.getToppings() : new ArrayList<>();
                List<ToppingResponse> toppingRes = chiTietToppings.stream()
                        .map(t -> ToppingResponse.builder()
                                .ten(t.getTen() != null && !t.getTen().isBlank()
                                        ? t.getTen()
                                        : (t.getNguyenLieu() != null ? t.getNguyenLieu().getTen() : "Topping"))
                                .soLuong(t.getSoLuong() != null ? t.getSoLuong() : 1)
                                .giaTien(t.getDonGia())
                                .build())
                        .toList();

                TuyChinhKhachHang tc2 = ct.getTuyChinh();
                return ChiTietDonHangResponse.builder()
                        .idChiTietDonHang(ct.getId())
                        .tenSanPham(ct.getNuocUong().getTen())
                        .giaTien(ct.getNuocUong().getGia())
                        .mucDa(tc2 != null ? tc2.getMucDa() : null)
                        .ghiChu(tc2 != null ? tc2.getGhiChu() : null)
                        .soLuong(ct.getSoLuong())
                        .thanhTien(ct.getThanhTien())
                        .toppings(toppingRes)
                        .congThuc(mapToCongThucResponse(ct.getNuocUong()))
                        .build();
            })
            .toList();
        
        BigDecimal tienGiam = donHang.getTienGiamApDung() != null ? donHang.getTienGiamApDung() : BigDecimal.ZERO;
        DonHangResponse response = DonHangResponse.builder()
                .id(donHang.getId())
                .ngayDat(donHang.getNgayDat())
                .trangThai(donHang.getTrangThaiCode())
                .phuongThucThanhToan(donHang.getThanhToan() != null ? donHang.getThanhToan().getPhuongThuc().name() : null)
                .trangThaiThanhToan(donHang.getThanhToan() != null ? donHang.getThanhToan().getTrangThai().name() : null)
                .tenKhachHang(donHang.getKhachHang() != null ? (donHang.getKhachHang().getHoTen() != null ? donHang.getKhachHang().getTenDangNhap() : donHang.getKhachHang().getHoTen()) : "Khách vãng lai")
                .tamTinhHang(tamTinhHang)
                .tienGiamApDung(tienGiam)
                .maGiamGia(donHang.getMaGiamGia() != null ? donHang.getMaGiamGia().getMa() : null)
                .tongTien(donHang.getTongTien())
                .chiTietDonHang(chiTietResponses)
                .build();
        return response;
    }

    

    // CHeck if add same ChiTietDonHang again, if yes then update quantity and price instead of adding new item

    private boolean KiemTraChiTietDonHangGiongNhau(ChiTietDonHang chiTiet1, ChiTietDonHang chiTiet2) {
        if (!chiTiet1.getNuocUong().getId().equals(chiTiet2.getNuocUong().getId())) {
            return false;
        }

        TuyChinhKhachHang tc1 = chiTiet1.getTuyChinh();
        TuyChinhKhachHang tc2 = chiTiet2.getTuyChinh();

        if (tc1 == null && tc2 == null) {
            return true; // Cả hai đều không có tùy chỉnh, coi như giống nhau
        }

        if (tc1 == null || tc2 == null) {
            return false; // Một trong hai có tùy chỉnh, một cái không, coi như khác nhau
        }

        // So sánh các toppings từng loại topping xem có trùng không
        List<ChiTietTopping> toppings1 = chiTiet1.getToppings() != null ? chiTiet1.getToppings() : new ArrayList<>();
        List<ChiTietTopping> toppings2 = chiTiet2.getToppings() != null ? chiTiet2.getToppings() : new ArrayList<>();
        if (toppings1.size() != toppings2.size()) {
            return false; // Số lượng loại topping khác nhau, coi như khác nhau
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

                if (sameIngredientId &&
                    soLuong1 == soLuong2 &&
                    donGia1.compareTo(donGia2) == 0) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                return false; // Không tìm thấy loại topping tương ứng, coi như khác nhau
            }
        }


        // So sánh các thuộc tính tùy chỉnh
        return Objects.equals(tc1.getMucDa(), tc2.getMucDa()) &&
               ((tc1.getGhiChu() == null && tc2.getGhiChu() == null) ||
                (tc1.getGhiChu() != null && tc1.getGhiChu().equals(tc2.getGhiChu())));
    }

    private static void damBaoLaTopping(NguyenLieu nl) {
        if (nl == null || nl.getLoaiNguyenLieu() != LoaiNguyenLieu.TOPPING) {
            throw new IllegalArgumentException("Chi chap nhan nguyen lieu loai TOPPING lam topping");
        }
    }

    private DiaChi resolveDiaChiGiaoHang(UUID khachHangId, UUID diaChiId) {
        if (diaChiId != null) {
            DiaChi diaChi = diaChiRepository.findById(diaChiId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ"));
            if (!diaChi.getKhachHang().getId().equals(khachHangId)) {
                throw new SecurityException("Địa chỉ không thuộc về khách hàng này");
            }
            return diaChi;
        }

        return diaChiRepository.findFirstByKhachHangIdAndLaMacDinhTrue(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ mặc định"));
    }
}
