package com.cgv.repository.customer;

import com.cgv.entity.customer.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Integer> {

    Optional<Membership> findByCustomerCUserId(String cUserId);
}
