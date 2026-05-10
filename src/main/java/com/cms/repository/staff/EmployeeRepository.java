package com.cms.repository.staff;

import com.cms.entity.staff.Employee;
import com.cms.common.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByBranchBranchId(Integer branchId);

    List<Employee> findByUserType(UserType userType);

    List<Employee> findByManagerEUserId(String managerEUserId);

    @Modifying
    @Query(value = "DELETE FROM work WHERE start_time = :st AND end_time = :et AND w_date = :wd", nativeQuery = true)
    void unassignAllEmployeesFromShift(@Param("st") LocalTime startTime,
                                       @Param("et") LocalTime endTime,
                                       @Param("wd") Integer wDate);
}
