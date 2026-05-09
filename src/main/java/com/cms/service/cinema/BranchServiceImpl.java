package com.cms.service.cinema;

import com.cms.common.exception.AppException;
import com.cms.dto.request.BranchRequest;
import com.cms.dto.response.BranchResponse;
import com.cms.entity.cinema.Branch;
import com.cms.entity.staff.Employee;
import com.cms.repository.cinema.BranchRepository;
import com.cms.repository.staff.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    private BranchResponse toResponse(Branch branch) {
        BranchResponse response = modelMapper.map(branch, BranchResponse.class);
        if (branch.getManager() != null) {
            response.setManagerName(branch.getManager().getEName());
            response.setManagerId(branch.getManager().getEUserId());
        }
        response.setTotalRooms(branch.getScreenRooms() != null ? branch.getScreenRooms().size() : 0);
        return response;
    }

    private void applyRequest(Branch branch, BranchRequest request) {
        modelMapper.map(request, branch);
        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> AppException.notFound("Employee", request.getManagerId()));
            branch.setManager(manager);
        } else {
            branch.setManager(null);
        }
        branch.setPhoneNumbers(request.getPhoneNumbers() != null
                ? request.getPhoneNumbers() : new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("branches")
    public List<BranchResponse> getAll() {
        return branchRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "branches", key = "#id")
    public BranchResponse getById(Integer id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Branch", id));
        return toResponse(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> searchByName(String name) {
        return branchRepository.findByBNameContainingIgnoreCase(name).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "branches", allEntries = true)
    public BranchResponse create(BranchRequest request) {
        Branch branch = Branch.builder().build();
        applyRequest(branch, request);
        return toResponse(branchRepository.save(branch));
    }

    @Override
    @CacheEvict(value = "branches", allEntries = true)
    public BranchResponse update(Integer id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Branch", id));
        applyRequest(branch, request);
        return toResponse(branchRepository.save(branch));
    }

    @Override
    @CacheEvict(value = "branches", allEntries = true)
    public void delete(Integer id) {
        if (!branchRepository.existsById(id)) {
            throw AppException.notFound("Branch", id);
        }
        branchRepository.deleteById(id);
    }
}
