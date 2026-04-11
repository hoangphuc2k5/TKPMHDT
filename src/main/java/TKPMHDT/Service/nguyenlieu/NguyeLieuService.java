package TKPMHDT.Service.nguyenlieu;

import java.util.List;

import org.springframework.stereotype.Service;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import lombok.RequiredArgsConstructor;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;

@Service
@RequiredArgsConstructor
public class NguyeLieuService {
    private final NguyenLieuRepository nguyeLieuRepository;

    //Lấy các nguyên liệu là topping
    public List<NguyenLieu> layNguyenLieuTopping() {
        return nguyeLieuRepository.findAllNguyenLieuByLoaNguyenLieu(LoaiNguyenLieu.TOPPING);
    }
}
