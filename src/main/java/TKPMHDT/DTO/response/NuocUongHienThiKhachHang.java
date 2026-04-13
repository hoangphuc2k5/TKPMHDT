package TKPMHDT.DTO.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dữ liệu sản phẩm hiển thị cho khách (kèm khuyến mãi giá nếu có). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NuocUongHienThiKhachHang {

    private UUID id;
    private String ten;
    /** Giá niêm yết (gốc). */
    private BigDecimal gia;
    /** Giá sau khuyến mãi trực tiếp (bằng gia nếu không có KM). */
    private BigDecimal giaSauKhuyenMai;
    private String moTa;
    private String danhMuc;
    @Builder.Default
    private List<String> hinhAnh = new ArrayList<>();
    private Boolean dangKinhDoanh;

    private boolean dangKhuyenMai;
    /** Nhãn ngắn: ví dụ "-20%", "Giảm 15.000₫", "Khuyến mãi". */
    private String nhanKhuyenMai;
}
