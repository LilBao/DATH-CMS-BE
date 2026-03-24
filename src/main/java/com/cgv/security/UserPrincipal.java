package com.cgv.security;

import com.cgv.entity.customer.Customer;
import com.cgv.entity.staff.Employee;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserPrincipal - Wrapper cho Spring Security UserDetails.
 * Hỗ trợ cả Customer và Employee.
 */
@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private final String userId;      // CUSxxx hoặc EMPxxx
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isActive;

    // ── Static factory methods ──────────────────────────────────

    /**
     * Tạo từ Customer entity
     */
    public static UserPrincipal fromCustomer(Customer customer) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + customer.getUserType().name())
        );
        return UserPrincipal.builder()
                .userId(customer.getCUserId())
                .email(customer.getEmail())
                .password(customer.getEPassword())
                .authorities(authorities)
                .isActive(customer.isActive())
                .build();
    }

    /**
     * Tạo từ Employee entity
     */
    public static UserPrincipal fromEmployee(Employee employee) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + employee.getUserType().name())
        );
        return UserPrincipal.builder()
                .userId(employee.getEUserId())
                .email(employee.getEmail())
                .password(employee.getEPassword())
                .authorities(authorities)
                .isActive(employee.isActive())
                .build();
    }

    // ── UserDetails overrides ────────────────────────────────────

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
