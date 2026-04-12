package com.cms.service.screening;

import com.cms.common.exception.AppException;
import com.cms.dto.request.ShowtimeRequest;
import com.cms.dto.response.ShowtimeResponse;
import com.cms.entity.cinema.ScreenRoom;
import com.cms.entity.cinema.ScreenRoomId;
import com.cms.entity.movie.Format;
import com.cms.entity.movie.Movie;
import com.cms.entity.screening.Showtime;
import com.cms.enums.EShowtimeStatus;
import com.cms.repository.cinema.ScreenRoomRepository;
import com.cms.repository.movie.FormatRepository;
import com.cms.repository.movie.MovieRepository;
import com.cms.repository.screening.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ScreenRoomRepository screenRoomRepository;
    private final FormatRepository formatRepository;
    private final ModelMapper modelMapper;

    private ShowtimeResponse toResponse(Showtime s) {
        ShowtimeResponse response = modelMapper.map(s, ShowtimeResponse.class);
        if (s.getMovie() != null) {
            response.setMovieId(s.getMovie().getMovieId());
            response.setMovieName(s.getMovie().getMName());
        }
        if (s.getScreenRoom() != null) {
            response.setBranchId(s.getScreenRoom().getId().getBranchId());
            if (s.getScreenRoom().getBranch() != null) {
                response.setBranchName(s.getScreenRoom().getBranch().getBName());
            }
            response.setRoomId(s.getScreenRoom().getId().getRoomId());
        }
        if (s.getFormat() != null) {
            response.setFormatName(s.getFormat().getFName());
        }
        return response;
    }

    private Showtime buildFromRequest(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> AppException.notFound("Movie", request.getMovieId()));
        ScreenRoomId pk = new ScreenRoomId(request.getBranchId(), request.getRoomId());
        ScreenRoom room = screenRoomRepository.findById(pk)
                .orElseThrow(() -> AppException.notFound("ScreenRoom",
                        request.getBranchId() + "/" + request.getRoomId()));
        Format format = null;
        if (request.getFormatName() != null) {
            format = formatRepository.findById(request.getFormatName())
                    .orElseThrow(() -> AppException.notFound("Format", request.getFormatName()));
        }
        return Showtime.builder()
                .movie(movie)
                .screenRoom(room)
                .format(format)
                .day(request.getDay())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(EShowtimeStatus.SCHEDULED)
                .tickets(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getAll() {
        return showtimeRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShowtimeResponse getById(Integer id) {
        return toResponse(showtimeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Showtime", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getByMovieAndDay(Integer movieId, LocalDate day) {
        return showtimeRepository.findByMovieAndDay(movieId, day).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getByBranchAndDay(Integer branchId, LocalDate day) {
        return showtimeRepository.findByBranchAndDay(branchId, day).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getByMovie(Integer movieId) {
        return showtimeRepository.findByMovieMovieId(movieId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ShowtimeResponse create(ShowtimeRequest request) {
        // Kiểm tra conflict suất chiếu
        boolean conflict = showtimeRepository
                .existsByScreenRoomIdBranchIdAndScreenRoomIdRoomIdAndDayAndStartTime(
                        request.getBranchId(), request.getRoomId(),
                        request.getDay(), request.getStartTime());
        if (conflict) {
            throw AppException.conflict("Showtime already scheduled at this room/time slot");
        }
        return toResponse(showtimeRepository.save(buildFromRequest(request)));
    }

    @Override
    public ShowtimeResponse update(Integer id, ShowtimeRequest request) {
        Showtime existing = showtimeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Showtime", id));
        Showtime updated = buildFromRequest(request);
        updated.setTimeId(id);
        updated.setStatus(existing.getStatus());
        return toResponse(showtimeRepository.save(updated));
    }

    @Override
    public ShowtimeResponse updateStatus(Integer id, String status) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Showtime", id));
        try {
            showtime.setStatus(EShowtimeStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw AppException.badRequest("Invalid status: " + status);
        }
        return toResponse(showtimeRepository.save(showtime));
    }

    @Override
    public void delete(Integer id) {
        if (!showtimeRepository.existsById(id)) {
            throw AppException.notFound("Showtime", id);
        }
        showtimeRepository.deleteById(id);
    }
}
