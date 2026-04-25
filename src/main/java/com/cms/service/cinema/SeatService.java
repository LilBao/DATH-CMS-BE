package com.cms.service.cinema;

import com.cms.dto.request.SeatRequest;
import com.cms.dto.response.SeatResponse;


import java.util.List;

public interface SeatService {
    List<SeatResponse> getByRoom(Integer branchId, Integer roomId);
    SeatResponse create(SeatRequest request);
    SeatResponse update(SeatRequest request);
    void delete(Integer branchId, Integer roomId, Integer sRow, Integer sColumn);
    void createBulk(List<SeatRequest> requests);
    List<SeatResponse> getByShowtime(Integer timeId);
}
