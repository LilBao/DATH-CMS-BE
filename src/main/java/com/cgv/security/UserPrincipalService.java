package com.cgv.security;

import com.cgv.entity.customer.Customer;
import com.cgv.entity.staff.Employee;
import com.cgv.repository.customer.CustomerRepository;
import com.cgv.repository.staff.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Triển khai UserDetailsService - Spring Security dùng để load user.
 * Tìm kiếm theo email trong cả Customer và Employee.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm trong Customer trước
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return UserPrincipal.fromCustomer(customer.get());
        }

        // Tìm trong Employee
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isPresent()) {
            return UserPrincipal.fromEmployee(employee.get());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
