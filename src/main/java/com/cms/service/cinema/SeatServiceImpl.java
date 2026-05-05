package com.cms.service.cinema;

import com.cms.common.exception.AppException;
import com.cms.dto.request.SeatRequest;
import com.cms.dto.response.SeatResponse;
import com.cms.entity.cinema.ScreenRoom;
import com.cms.entity.cinema.ScreenRoomId;
import com.cms.entity.cinema.Seat;
import com.cms.entity.cinema.SeatId;
import com.cms.entity.screening.Showtime;
import com.cms.entity.screening.Ticket;
import com.cms.enums.ETicketStatus;
import com.cms.repository.cinema.ScreenRoomRepository;
import com.cms.repository.cinema.SeatRepository;
import com.cms.repository.screening.ShowtimeRepository;
import com.cms.repository.screening.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final ScreenRoomRepository screenRoomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final TicketRepository ticketRepository;
    private final ModelMapper modelMapper;

    private SeatResponse toResponse(Seat seat) {
        SeatResponse response = modelMapper.map(seat, SeatResponse.class);
        response.setBranchId(seat.getId().getBranchId());
        response.setRoomId(seat.getId().getRoomId());
        response.setSRow(seat.getId().getSRow());
        response.setSColumn(seat.getId().getSColumn());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "seats_by_room", key = "#branchId.toString() + '_' + #roomId.toString()")
    public List<SeatResponse> getByRoom(Integer branchId, Integer roomId) {
        return seatRepository.findByIdBranchIdAndIdRoomId(branchId, roomId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "seats_by_room", key = "#request.branchId.toString() + '_' + #request.roomId.toString()")
    public SeatResponse create(SeatRequest request) {
        ScreenRoomId roomPk = new ScreenRoomId(request.getBranchId(), request.getRoomId());
        ScreenRoom room = screenRoomRepository.findById(roomPk)
                .orElseThrow(() -> AppException.notFound("ScreenRoom", request.getBranchId() + "/" + request.getRoomId()));

        SeatId seatPk = new SeatId(request.getBranchId(), request.getRoomId(), request.getSRow(), request.getSColumn());
        if (seatRepository.existsById(seatPk)) {
            throw AppException.conflict("Seat already exists at this position: Row " + request.getSRow() + ", Col " + request.getSColumn());
        }

        Seat seat = Seat.builder()
                .id(seatPk)
                .sType(request.getSType() != null ? request.getSType() : 0)
                .sPrice(request.getSPrice())
                .sStatus(request.getSStatus() != null ? request.getSStatus() : true)
                .screenRoom(room)
                .build();

        return toResponse(seatRepository.save(seat));
    }

    @Override
    @CacheEvict(value = "seats_by_room", key = "#request.branchId.toString() + '_' + #request.roomId.toString()")
    public SeatResponse update(SeatRequest request) {
        SeatId seatPk = new SeatId(request.getBranchId(), request.getRoomId(), request.getSRow(), request.getSColumn());
        Seat seat = seatRepository.findById(seatPk)
                .orElseThrow(() -> AppException.notFound("Seat", "Row " + request.getSRow() + ", Col " + request.getSColumn()));

        if (request.getSType() != null) {
            seat.setSType(request.getSType());
        }
        if (request.getSPrice() != null) {
            seat.setSPrice(request.getSPrice());
        }
        if (request.getSStatus() != null) {
            seat.setSStatus(request.getSStatus());
        }

        return toResponse(seatRepository.save(seat));
    }

    @Override
    @CacheEvict(value = "seats_by_room", key = "#branchId.toString() + '_' + #roomId.toString()")
    public void delete(Integer branchId, Integer roomId, Integer sRow, Integer sColumn) {
        SeatId seatPk = new SeatId(branchId, roomId, sRow, sColumn);
        if (!seatRepository.existsById(seatPk)) {
            throw AppException.notFound("Seat", "Row " + sRow + ", Col " + sColumn);
        }
        seatRepository.deleteById(seatPk);
    }

    @Override
    @CacheEvict(value = "seats_by_room", allEntries = true)
    public void createBulk(List<SeatRequest> requests) {
        if (requests == null || requests.isEmpty()) return;
        
        for (SeatRequest request : requests) {
            try {
                create(request);
            } catch (Exception e) {
                // For bulk, maybe we want to continue or throw. 
                // Let's just let it throw for now to be safe, or log it.
                throw e;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getByShowtime(Integer timeId) {
        Showtime showtime = showtimeRepository.findById(timeId)
                .orElseThrow(() -> AppException.notFound("Showtime", timeId.toString()));

        Integer branchId = showtime.getScreenRoom().getId().getBranchId();
        Integer roomId = showtime.getScreenRoom().getId().getRoomId();

        List<Seat> allSeats = seatRepository.findByIdBranchIdAndIdRoomId(branchId, roomId);
        List<Ticket> bookedTickets = ticketRepository.findByShowtimeTimeIdAndTicketStatusNot(timeId, ETicketStatus.REFUNDED);

        Set<String> bookedSeatKeys = bookedTickets.stream()
                .map(t -> t.getSeat().getId().getSRow() + "_" + t.getSeat().getId().getSColumn())
                .collect(Collectors.toSet());

        return allSeats.stream()
                .map(seat -> {
                    SeatResponse res = toResponse(seat);
                    res.setIsBooked(bookedSeatKeys.contains(seat.getId().getSRow() + "_" + seat.getId().getSColumn()));
                    return res;
                })
                .collect(Collectors.toList());
    }
}
