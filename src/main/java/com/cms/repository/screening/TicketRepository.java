package com.cms.repository.screening;

import com.cms.entity.screening.Ticket;
import com.cms.enums.ETicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByShowtimeTimeIdAndTicketStatusNot(Integer timeId, ETicketStatus status);

    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.order.customer.cUserId = :cUserId AND t.showtime.movie.movieId = :movieId AND t.ticketStatus IN :statuses")
    boolean existsByOrderCustomer(
            @Param("cUserId") String cUserId, 
            @Param("movieId") Integer movieId, 
            @Param("statuses") Collection<ETicketStatus> statuses);
}
