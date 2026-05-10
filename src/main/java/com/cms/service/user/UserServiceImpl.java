package com.cms.service.user;

import com.cms.common.exception.AppException;
import com.cms.dto.request.ChangePasswordRequest;
import com.cms.entity.customer.Customer;
import com.cms.entity.staff.Employee;
import com.cms.repository.customer.CustomerRepository;
import com.cms.repository.staff.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        if (userId.startsWith("CUS")) {
            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() -> AppException.notFound("Customer", userId));

            if (!passwordEncoder.matches(request.getOldPassword(), customer.getEPassword())) {
                throw AppException.badRequest("Old password does not match");
            }

            customer.setEPassword(passwordEncoder.encode(request.getNewPassword()));
            customerRepository.save(customer);
        } else if (userId.startsWith("EMP")) {
            Employee employee = employeeRepository.findById(userId)
                    .orElseThrow(() -> AppException.notFound("Employee", userId));

            if (!passwordEncoder.matches(request.getOldPassword(), employee.getEPassword())) {
                throw AppException.badRequest("Old password does not match");
            }

            employee.setEPassword(passwordEncoder.encode(request.getNewPassword()));
            employeeRepository.save(employee);
        } else {
            throw AppException.badRequest("Invalid user ID format");
        }
    }
}
