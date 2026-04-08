package com.cms.service.cinema;

import com.cms.dto.request.BranchRequest;
import com.cms.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {
    List<BranchResponse> getAll();
    BranchResponse getById(Integer id);
    List<BranchResponse> searchByName(String name);
    BranchResponse create(BranchRequest request);
    BranchResponse update(Integer id, BranchRequest request);
    void delete(Integer id);
}
