package com.cms.service.booking;

import com.cms.dto.request.OrderRequest;
import com.cms.dto.response.OrderResponse;
import com.cms.enums.EOrderStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getAll(EOrderStatus status);
    OrderResponse getById(String id);
    OrderResponse getByEmail(String email);
    OrderResponse createOrder(UserDetails userDetails, OrderRequest request);
}