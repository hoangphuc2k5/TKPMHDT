package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.sanpham.TuyChonTuyChinh;
import TKPMHDT.Repository.sanpham.TuyChonTuyChinhRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminTuyChonController {

    private final TuyChonTuyChinhRepository tuyChonTuyChinhRepository;
    private final AdminAuditHelper audit;

    public AdminTuyChonController(TuyChonTuyChinhRepository tuyChonTuyChinhRepository, AdminAuditHelper audit) {
        this.tuyChonTuyChinhRepository = tuyChonTuyChinhRepository;
        this.audit = audit;
    }

    @GetMapping("/tuy-chon")
    public ResponseEntity<List<TuyChonTuyChinh>> layTuyChon() {
        return ResponseEntity.ok(tuyChonTuyChinhRepository.findAll());
    }

    @PostMapping("/tuy-chon")
    public ResponseEntity<TuyChonTuyChinh> taoTuyChon(@RequestBody AdminApiDtos.TuyChonRequest request) {
        TuyChonTuyChinh entity = TuyChonTuyChinh.builder()
                .ten(request.ten())
                .nhom(request.nhom())
                .giaThem(request.giaThem() != null ? request.giaThem() : BigDecimal.ZERO)
                .kichHoat(request.kichHoat() == null || request.kichHoat())
                .build();
        TuyChonTuyChinh saved = tuyChonTuyChinhRepository.save(entity);
        audit.ghiLog("TUY_CHON", "TAO", saved.getTen());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/tuy-chon/{id}")
    public ResponseEntity<TuyChonTuyChinh> capNhatTuyChon(
            @PathVariable UUID id, @RequestBody AdminApiDtos.TuyChonRequest request) {
        return tuyChonTuyChinhRepository
                .findById(id)
                .map(entity -> {
                    if (request.ten() != null) {
                        entity.setTen(request.ten());
                    }
                    if (request.nhom() != null) {
                        entity.setNhom(request.nhom());
                    }
                    if (request.giaThem() != null) {
                        entity.setGiaThem(request.giaThem());
                    }
                    if (request.kichHoat() != null) {
                        entity.setKichHoat(request.kichHoat());
                    }
                    TuyChonTuyChinh updated = tuyChonTuyChinhRepository.save(entity);
                    audit.ghiLog("TUY_CHON", "CAP_NHAT", updated.getTen());
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tuy-chon/{id}")
    public ResponseEntity<Map<String, Object>> xoaTuyChon(@PathVariable UUID id) {
        tuyChonTuyChinhRepository.deleteById(id);
        audit.ghiLog("TUY_CHON", "XOA", id.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
