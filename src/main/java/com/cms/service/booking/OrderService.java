package com.cms.service.booking;

import com.cms.dto.request.OrderRequest;
import com.cms.dto.response.OrderResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getAll();
    OrderResponse getById(String id);
    OrderResponse getByEmail(String email);
    OrderResponse createOrder(UserDetails userDetails, OrderRequest request);
}