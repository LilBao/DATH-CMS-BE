package com.cms.service.customer;

import com.cms.common.exception.AppException;
import com.cms.dto.request.UpdateProfileRequest;
import com.cms.dto.response.CustomerResponse;
import com.cms.entity.customer.Customer;
import com.cms.enums.ERank;
import com.cms.repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    private static final String[] RANK_NAMES = {"", "Bronze", "Silver", "Gold", "Diamond"};

    private CustomerResponse toResponse(Customer c) {
        CustomerResponse response = modelMapper.map(c, CustomerResponse.class);
        if (c.getMembership() != null) {
            ERank rank = c.getMembership().getMemberRank();
            response.setMembershipTier((rank.getLabel() >= 1 && rank.getLabel() <= 4) ? rank.name() : "UNKNOWN");
            response.setTotalPoints(c.getMembership().getPoint());
        }
        response.setAvatarUrl(c.getAvatarUrl());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(String id) {
        return toResponse(customerRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Customer", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getByEmail(String email) {
        return toResponse(customerRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("Customer", email)));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getByPhone(String phone) {
        return toResponse(customerRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> AppException.notFound("Customer phone: ", phone)));
    }

    @Override
    public CustomerResponse updateProfile(String userId, UpdateProfileRequest request) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Customer", userId));

        customer.setCName(request.getName());
        customer.setSex(request.getSex());
        customer.setBirthday(request.getBirthday());
        customer.setPhoneNumber(request.getPhoneNumber());
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
            customer.setAvatarUrl(request.getAvatarUrl());
        }

        return toResponse(customerRepository.save(customer));
    }

    @Override
    public void deactivate(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Customer", id));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    @Override
    public void activate(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Customer", id));
        customer.setActive(true);
        customerRepository.save(customer);
    }

    @Override
    public void delete(String id) {
        if (!customerRepository.existsById(id)) {
            throw AppException.notFound("Customer", id);
        }
        customerRepository.deleteById(id);
    }
}
