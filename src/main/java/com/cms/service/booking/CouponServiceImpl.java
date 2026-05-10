package com.cms.service.booking;

import com.cms.common.exception.AppException;
import com.cms.dto.request.CouponRequest;
import com.cms.dto.response.CouponResponse;
import com.cms.entity.booking.Coupon;
import com.cms.repository.booking.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream()
                .map(c -> modelMapper.map(c, CouponResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getById(Integer id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Coupon", id.toString()));
        return modelMapper.map(coupon, CouponResponse.class);
    }

    @Override
    public CouponResponse create(CouponRequest request) {
        Coupon coupon = modelMapper.map(request, Coupon.class);
        return modelMapper.map(couponRepository.save(coupon), CouponResponse.class);
    }

    @Override
    public CouponResponse update(Integer id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Coupon", id.toString()));
        modelMapper.map(request, coupon);
        coupon.setCouponId(id);
        return modelMapper.map(couponRepository.save(coupon), CouponResponse.class);
    }

    @Override
    public void delete(Integer id) {
        if (!couponRepository.existsById(id)) {
            throw AppException.notFound("Coupon", id.toString());
        }
        couponRepository.deleteById(id);
    }
}
