package com.cms.repository.cinema;

import com.cms.entity.cinema.Seat;
import com.cms.entity.cinema.SeatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, SeatId> {
    List<Seat> findByIdBranchIdAndIdRoomId(Integer branchId, Integer roomId);
}
