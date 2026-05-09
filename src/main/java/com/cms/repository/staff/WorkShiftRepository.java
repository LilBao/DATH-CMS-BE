package com.cms.repository.staff;

import com.cms.entity.staff.WorkShift;
import com.cms.entity.staff.WorkShiftId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, WorkShiftId> {
}