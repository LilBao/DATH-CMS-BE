package com.cms.repository.screening;

import com.cms.entity.screening.Ticket;
import com.cms.enums.ETicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByShowtimeTimeIdAndTicketStatusNot(Integer timeId, ETicketStatus status);

    boolean existsByOrderCustomer(
            String cUserId, Integer movieId, java.util.Collection<ETicketStatus> statuses);
}
