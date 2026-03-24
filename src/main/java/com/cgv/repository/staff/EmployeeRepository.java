package com.cgv.repository.staff;

import com.cgv.entity.staff.Employee;
import com.cgv.common.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByBranchBranchId(Integer branchId);

    List<Employee> findByUserType(UserType userType);

    List<Employee> findByManagerEUserId(String managerEUserId);
}
