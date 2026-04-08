package com.cms.service.customer;

import com.cms.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAll();
    CustomerResponse getById(String id);
    CustomerResponse getByEmail(String email);
    void deactivate(String id);
    void activate(String id);
    void delete(String id);
}
