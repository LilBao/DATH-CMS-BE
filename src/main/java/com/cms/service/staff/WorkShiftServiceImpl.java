package com.cms.service.staff;

import com.cms.common.exception.AppException;
import com.cms.dto.request.WorkShiftRequest;
import com.cms.dto.response.WorkShiftResponse;
import com.cms.entity.staff.Employee;
import com.cms.entity.staff.WorkShift;
import com.cms.entity.staff.WorkShiftId;
import com.cms.repository.staff.EmployeeRepository;
import com.cms.repository.staff.WorkShiftRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkShiftServiceImpl implements WorkShiftService {

    private final WorkShiftRepository workShiftRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    private WorkShiftResponse toResponse(WorkShift workShift) {
        if (workShift == null) return null;
        return WorkShiftResponse.builder()
                .startTime(workShift.getId().getStartTime())
                .endTime(workShift.getId().getEndTime())
                .wDate(workShift.getId().getWDate())
                .work(workShift.getWork())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<WorkShiftResponse> getByBranch(Integer branchId) {
        List<Employee> employees = employeeRepository.findByBranchBranchId(branchId);

        Set<WorkShift> shiftsInBranch = employees.stream()
                .flatMap(e -> e.getWorkShifts().stream())
                .collect(Collectors.toSet());

        return shiftsInBranch.stream().map(shift -> {
            WorkShiftResponse res = toResponse(shift);
            List<com.cms.dto.response.EmployeeResponse> staffInShift = employees.stream()
                    .filter(e -> e.getWorkShifts().contains(shift))
                    .map(e -> modelMapper.map(e, com.cms.dto.response.EmployeeResponse.class))
                    .collect(Collectors.toList());
            res.setEmployees(staffInShift);
            return res;
        }).collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<WorkShiftResponse> getAll() {
        return workShiftRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkShiftResponse getById(LocalTime startTime, LocalTime endTime, Integer wDate) {
        WorkShiftId id = new WorkShiftId(startTime, endTime, wDate);
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("WorkShift", "PK: " + startTime + "-" + endTime + "-" + wDate));
        return toResponse(workShift);
    }

    @Override
    public WorkShiftResponse create(WorkShiftRequest request) {
        WorkShiftId id = new WorkShiftId(request.getStartTime(), request.getEndTime(), request.getWDate());
        if (workShiftRepository.existsById(id)) {
            throw AppException.conflict("WorkShift already exists!");
        }

        WorkShift workShift = WorkShift.builder()
                .id(id)
                .work(request.getWork())
                .build();
        return toResponse(workShiftRepository.save(workShift));
    }

    @Override
    public WorkShiftResponse update(LocalTime startTime, LocalTime endTime, Integer wDate, WorkShiftRequest request) {
        WorkShiftId id = new WorkShiftId(startTime, endTime, wDate);
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("WorkShift", "PK: " + startTime + "-" + endTime + "-" + wDate));

        workShift.setWork(request.getWork());

        return toResponse(workShiftRepository.save(workShift));
    }

    @Override
    public void delete(LocalTime startTime, LocalTime endTime, Integer wDate) {
        WorkShiftId id = new WorkShiftId(startTime, endTime, wDate);
        if (!workShiftRepository.existsById(id)) {
            throw AppException.notFound("WorkShift", "PK: " + startTime + "-" + endTime + "-" + wDate);
        }

        try {
            workShiftRepository.deleteById(id);
            workShiftRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Không thể xóa khung giờ này vì đang có nhân viên được phân công. Vui lòng gỡ ca làm của nhân viên trước khi xóa.");
        }
    }
}