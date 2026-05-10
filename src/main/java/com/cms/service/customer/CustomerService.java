package com.cms.service.customer;

import com.cms.dto.request.UpdateProfileRequest;
import com.cms.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAll();
    CustomerResponse getById(String id);
    CustomerResponse getByEmail(String email);
    CustomerResponse getByPhone(String phone);
    CustomerResponse updateProfile(String userId, UpdateProfileRequest request);
    void deactivate(String id);
    void activate(String id);
    void delete(String id);
}
