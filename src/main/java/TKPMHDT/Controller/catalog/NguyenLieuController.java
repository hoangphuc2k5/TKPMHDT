package TKPMHDT.Controller.catalog;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Service.nguyenlieu.NguyeLieuService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/nguyen-lieu")
@RequiredArgsConstructor
public class NguyenLieuController {

    private final NguyeLieuService nguyeLieuService;    
    
    @GetMapping("/topping")
    public ResponseEntity<List<NguyenLieu>> layNguyenLieuTopping() {
        return ResponseEntity.ok(nguyeLieuService.layNguyenLieuTopping());
    }
}
