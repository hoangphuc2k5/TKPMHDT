package com.example.nuocuong.service;

import com.example.nuocuong.dto.CongThucResponse;
import java.util.List;

public interface CongThucService {
    List<CongThucResponse> getAllCongThuc();
    CongThucResponse getCongThucById(Long id);
}
