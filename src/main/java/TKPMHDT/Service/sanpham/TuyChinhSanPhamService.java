package TKPMHDT.Service.sanpham;

import TKPMHDT.Entity.sanpham.LuongNguyenLieu;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TuyChinhSanPhamService - UC07: Tùy chỉnh sản phẩm
 * Mục đích: Cho phép khách hàng tùy chỉnh thành phần, đường, đá của sản phẩm
 */
@Service
public class TuyChinhSanPhamService {

    private final NuocUongSanRepository nuocUongSanRepository;
    private final NguyenLieuRepository nguyenLieuRepository;

    public TuyChinhSanPhamService(
            NuocUongSanRepository nuocUongSanRepository,
            NguyenLieuRepository nguyenLieuRepository
    ) {
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
    }

    /**
     * Kiểm tra xem sản phẩm có thể tùy chỉnh được không
     */
    @Transactional(readOnly = true)
    public boolean coTheTuyChinh(UUID sanPhamId) {
        NuocUongSan sanPham = nuocUongSanRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        return sanPham.isCoTheTuyChinh();
    }

    /**
     * Lấy danh sách nguyên liệu có thể thêm vào sản phẩm
     */
    @Transactional(readOnly = true)
    public List<NguyenLieu> layDanhSachNguyenLieuThem(UUID sanPhamId) {
        NuocUongSan sanPham = nuocUongSanRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        // Trả về tất cả nguyên liệu ngoài những cái trong công thức cơ bản
        List<NguyenLieu> toanBoNguyenLieu = nguyenLieuRepository.findAll();
        Set<NguyenLieu> nguyenLieuHienCo = sanPham.getNguyenLieuSuDung();

        return toanBoNguyenLieu.stream()
                .filter(nl -> !nguyenLieuHienCo.contains(nl))
                .toList();
    }

    /**
     * Tạo cấu hình tùy chỉnh sản phẩm
     * @param mucDuong - Mức đường (0-100%, tương ứng với số lượng đường)
     * @param mucDa - Mức đá (0-100%, tương ứng với khối lượng đá)
     * @param ghiChu - Ghi chú thêm
     */
    @Transactional(readOnly = true)
    public TuyChinhKhachHang taoTuyChinh(Integer mucDuong, Integer mucDa, String ghiChu) {
        // Validate
        if (mucDuong != null && (mucDuong < 0 || mucDuong > 100)) {
            throw new IllegalArgumentException("Mức đường phải trong khoảng 0-100");
        }
        if (mucDa != null && (mucDa < 0 || mucDa > 100)) {
            throw new IllegalArgumentException("Mức đá phải trong khoảng 0-100");
        }

        return TuyChinhKhachHang.builder()
                .mucDuong(mucDuong)
                .mucDa(mucDa)
                .ghiChu(ghiChu)
                .nguyenLieuThem(new java.util.ArrayList<>())
                .build();
    }

    /**
     * Thêm nguyên liệu tùy chỉnh vào danh sách
     */
    @Transactional(readOnly = true)
    public void themNguyenLieuTuyChinh(TuyChinhKhachHang tuyChinh, UUID nguyenLieuId, Integer soLuong) {
        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(nguyenLieuId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu"));

        LuongNguyenLieu luong = LuongNguyenLieu.builder()
                .nguyenLieu(nguyenLieu)
                .soLuong(soLuong == null ? java.math.BigDecimal.ZERO : java.math.BigDecimal.valueOf(soLuong))
                .donVi(nguyenLieu.getDonVi())
                .build();

        tuyChinh.getNguyenLieuThem().add(luong);
    }

    /**
     * Tính toán giá cuối cùng sau tùy chỉnh
     * Có thể mở rộng để tính toán chi phí nguyên liệu thêm
     */
    @Transactional(readOnly = true)
    public BigDecimal tinhGiaCuoiCung(UUID sanPhamId, TuyChinhKhachHang tuyChinh) {
        NuocUongSan sanPham = nuocUongSanRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        BigDecimal giaCoBan = sanPham.getGia() != null ? sanPham.getGia() : BigDecimal.ZERO;

        // Tính chi phí nguyên liệu thêm (có thể mở rộng)
        BigDecimal chiPhiThem = BigDecimal.ZERO;
        if (tuyChinh.getNguyenLieuThem() != null) {
            for (LuongNguyenLieu luong : tuyChinh.getNguyenLieuThem()) {
                if (luong.getNguyenLieu() != null && luong.getNguyenLieu().getGiaDonVi() != null) {
                    chiPhiThem = chiPhiThem.add(
                            luong.getNguyenLieu().getGiaDonVi().multiply(luong.getSoLuong() == null ? BigDecimal.ZERO : luong.getSoLuong())
                    );
                }
            }
        }

        return giaCoBan.add(chiPhiThem);
    }
}
