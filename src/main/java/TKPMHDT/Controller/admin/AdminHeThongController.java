package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.hethong.CauHinhHeThong;
import TKPMHDT.Entity.hethong.NhatKyHeThong;
import TKPMHDT.Entity.hethong.VaiTroQuyen;
import TKPMHDT.Repository.hethong.CauHinhHeThongRepository;
import TKPMHDT.Repository.hethong.NhatKyHeThongRepository;
import TKPMHDT.Repository.hethong.VaiTroQuyenRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminHeThongController {

    private final CauHinhHeThongRepository cauHinhHeThongRepository;
    private final NhatKyHeThongRepository nhatKyHeThongRepository;
    private final VaiTroQuyenRepository vaiTroQuyenRepository;
    private final AdminAuditHelper audit;

    public AdminHeThongController(
            CauHinhHeThongRepository cauHinhHeThongRepository,
            NhatKyHeThongRepository nhatKyHeThongRepository,
            VaiTroQuyenRepository vaiTroQuyenRepository,
            AdminAuditHelper audit) {
        this.cauHinhHeThongRepository = cauHinhHeThongRepository;
        this.nhatKyHeThongRepository = nhatKyHeThongRepository;
        this.vaiTroQuyenRepository = vaiTroQuyenRepository;
        this.audit = audit;
    }

    @GetMapping("/he-thong/cau-hinh")
    public ResponseEntity<List<CauHinhHeThong>> layCauHinhHeThong() {
        return ResponseEntity.ok(cauHinhHeThongRepository.findAll());
    }

    @PutMapping("/he-thong/cau-hinh")
    public ResponseEntity<CauHinhHeThong> luuCauHinhHeThong(@RequestBody AdminApiDtos.CauHinhRequest request) {
        CauHinhHeThong entity = cauHinhHeThongRepository
                .findByConfigKey(request.configKey())
                .orElseGet(CauHinhHeThong::new);
        entity.setConfigKey(request.configKey());
        entity.setConfigValue(request.configValue());
        entity.setMoTa(request.moTa());
        CauHinhHeThong saved = cauHinhHeThongRepository.save(entity);
        audit.ghiLog("HE_THONG", "CAU_HINH", saved.getConfigKey());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/he-thong/rbac")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<List<VaiTroQuyen>> layRbac() {
        damBaoRbacMacDinh();
        return ResponseEntity.ok(vaiTroQuyenRepository.findAll());
    }

    @PutMapping("/he-thong/rbac/{vaiTro}")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<VaiTroQuyen> capNhatRbac(
            @PathVariable String vaiTro, @RequestBody AdminApiDtos.RbacRequest request) {
        VaiTroQuyen entity = vaiTroQuyenRepository.findByVaiTro(vaiTro).orElseGet(VaiTroQuyen::new);
        entity.setVaiTro(vaiTro);
        entity.setQuyenCsv(String.join(",", request.quyens()));
        VaiTroQuyen saved = vaiTroQuyenRepository.save(entity);
        audit.ghiLog("RBAC", "CAP_NHAT", vaiTro);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/he-thong/log")
    public ResponseEntity<List<NhatKyHeThong>> layLogHeThong() {
        return ResponseEntity.ok(nhatKyHeThongRepository.findTop200ByOrderByThoiGianDesc());
    }

    private void damBaoRbacMacDinh() {
        taoRbacNeuChuaCo("QUAN_TRI_VIEN", List.of("ALL"));
        taoRbacNeuChuaCo("NHAN_VIEN_BAN_HANG", List.of("DON_HANG_XEM", "DON_HANG_CAP_NHAT"));
        taoRbacNeuChuaCo("KHACH_HANG", List.of("DON_HANG_CUA_TOI", "GIO_HANG"));
    }

    private void taoRbacNeuChuaCo(String role, List<String> permissions) {
        vaiTroQuyenRepository.findByVaiTro(role).orElseGet(() -> {
            VaiTroQuyen entity = new VaiTroQuyen();
            entity.setVaiTro(role);
            entity.setQuyenCsv(String.join(",", permissions));
            return vaiTroQuyenRepository.save(entity);
        });
    }
}
