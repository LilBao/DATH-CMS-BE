package com.cms.service.booking;

import com.cms.dto.request.CouponRequest;
import com.cms.dto.response.CouponResponse;

import java.util.List;

public interface CouponService {
    List<CouponResponse> getAll();
    CouponResponse getById(Integer id);
    CouponResponse create(CouponRequest request);
    CouponResponse update(Integer id, CouponRequest request);
    void delete(Integer id);
}
