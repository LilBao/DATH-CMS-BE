package com.cms.service.cinema;

import com.cms.common.exception.AppException;
import com.cms.dto.request.ScreenRoomRequest;
import com.cms.dto.response.ScreenRoomResponse;
import com.cms.entity.cinema.Branch;
import com.cms.entity.cinema.ScreenRoom;
import com.cms.entity.cinema.ScreenRoomId;
import com.cms.enums.ERType;
import com.cms.repository.cinema.BranchRepository;
import com.cms.repository.cinema.ScreenRoomRepository;
import com.cms.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScreenRoomServiceImpl implements ScreenRoomService {

    private final ScreenRoomRepository screenRoomRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    private ScreenRoomResponse toResponse(ScreenRoom room) {
        if (room == null) return null;
        return ScreenRoomResponse.builder()
                .branchId(room.getId().getBranchId())
                .roomId(room.getId().getRoomId())
                .rType(room.getRType() != null ? room.getRType().name() : null)
                .rCapacity(room.getRCapacity())
                .basePrice(room.getBasePrice())
                .totalSeats(room.getSeats() != null ? room.getSeats().size() : 0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreenRoomResponse> getAll() {
        return screenRoomRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreenRoomResponse> getByBranch(Integer branchId) {
        return screenRoomRepository.findByIdBranchId(branchId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScreenRoomResponse getById(Integer branchId, Integer roomId) {
        ScreenRoomId pk = new ScreenRoomId(branchId, roomId);
        ScreenRoom room = screenRoomRepository.findById(pk)
                .orElseThrow(() -> AppException.notFound("ScreenRoom", branchId + "/" + roomId));
        return toResponse(room);
    }

    @Override
    public ScreenRoomResponse create(ScreenRoomRequest request) {
        // Auto-fill branchId for Managers
        if (request.getBranchId() == null && SecurityUtil.isManager()) {
            request.setBranchId(SecurityUtil.getCurrentBranchId());
        }

        if (request.getBranchId() == null) {
            throw AppException.badRequest("Branch ID is required");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> AppException.notFound("Branch", request.getBranchId()));
        ScreenRoomId pk = new ScreenRoomId(request.getBranchId(), request.getRoomId());
        if (screenRoomRepository.existsById(pk)) {
            throw AppException.conflict("ScreenRoom already exists with id: "
                    + request.getBranchId() + "/" + request.getRoomId());
        }
        ScreenRoom room = ScreenRoom.builder()
                .id(pk)
                .rType(ERType.valueOf(request.getRType()))
                .rCapacity(request.getRCapacity())
                .basePrice(request.getBasePrice())
                .branch(branch)
                .seats(new ArrayList<>())
                .build();
        return toResponse(screenRoomRepository.save(room));
    }

    @Override
    public ScreenRoomResponse update(Integer branchId, Integer roomId, ScreenRoomRequest request) {
        ScreenRoomId pk = new ScreenRoomId(branchId, roomId);
        ScreenRoom room = screenRoomRepository.findById(pk)
                .orElseThrow(() -> AppException.notFound("ScreenRoom", branchId + "/" + roomId));
        
        if (request.getRType() != null) {
            room.setRType(ERType.valueOf(request.getRType()));
        }
        room.setRCapacity(request.getRCapacity());
        room.setBasePrice(request.getBasePrice());
        
        return toResponse(screenRoomRepository.save(room));
    }

    @Override
    public void delete(Integer branchId, Integer roomId) {
        ScreenRoomId pk = new ScreenRoomId(branchId, roomId);
        if (!screenRoomRepository.existsById(pk)) {
            throw AppException.notFound("ScreenRoom", branchId + "/" + roomId);
        }
        screenRoomRepository.deleteById(pk);
    }
}
