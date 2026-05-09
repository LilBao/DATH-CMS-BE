package com.cms.service.cinema;

import com.cms.dto.request.ScreenRoomRequest;
import com.cms.dto.response.ScreenRoomResponse;

import java.util.List;

public interface ScreenRoomService {
    List<ScreenRoomResponse> getAll();
    List<ScreenRoomResponse> getByBranch(Integer branchId);

    ScreenRoomResponse getById(Integer branchId, Integer roomId);
    ScreenRoomResponse create(ScreenRoomRequest request);
    ScreenRoomResponse update(Integer branchId, Integer roomId, ScreenRoomRequest request);
    void delete(Integer branchId, Integer roomId);
}
