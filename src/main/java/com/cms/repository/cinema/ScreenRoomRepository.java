package com.cms.repository.cinema;

import com.cms.entity.cinema.ScreenRoom;
import com.cms.entity.cinema.ScreenRoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRoomRepository extends JpaRepository<ScreenRoom, ScreenRoomId> {
    List<ScreenRoom> findByIdBranchId(Integer branchId);
}
