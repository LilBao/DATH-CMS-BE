package com.cms.service.staff;

import com.cms.common.exception.AppException;
import com.cms.dto.request.EmployeeRequest;
import com.cms.dto.request.WorkShiftRequest;
import com.cms.dto.response.EmployeeResponse;
import com.cms.entity.cinema.Branch;
import com.cms.entity.staff.Employee;
import com.cms.entity.staff.WorkShift;
import com.cms.entity.staff.WorkShiftId;
import com.cms.repository.cinema.BranchRepository;
import com.cms.repository.staff.EmployeeRepository;
import com.cms.repository.staff.WorkShiftRepository;
import com.cms.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkShiftRepository workShiftRepository;
    private final ModelMapper modelMapper;

    private EmployeeResponse toResponse(Employee e) {
        EmployeeResponse response = modelMapper.map(e, EmployeeResponse.class);
        if (e.getBranch() != null) {
            response.setBranchId(e.getBranch().getBranchId());
            response.setBranchName(e.getBranch().getBName());
        }
        if (e.getManager() != null) {
            response.setManagerId(e.getManager().getEUserId());
            response.setManagerName(e.getManager().getEName());
        }
        response.setAvatarUrl(e.getAvatarUrl());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(String id) {
        return toResponse(employeeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Employee", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getByBranch(Integer branchId) {
        return employeeRepository.findByBranchBranchId(branchId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsById(request.getEUserId())) {
            throw AppException.conflict("Employee ID already exists: " + request.getEUserId());
        }
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email already in use: " + request.getEmail());
        }

        Branch branch = null;

        if (SecurityUtil.isManager()) {
            Integer managerBranchId = SecurityUtil.getCurrentBranchId();
            if (managerBranchId == null) {
                throw AppException.badRequest("Tài khoản Manager của bạn không được gắn với chi nhánh nào!");
            }
            branch = branchRepository.findById(managerBranchId)
                    .orElseThrow(() -> AppException.notFound("Branch", managerBranchId));

        } else if (SecurityUtil.isAdmin()) {
            if (request.getBranchId() != null && request.getBranchId() > 0) {
                branch = branchRepository.findById(request.getBranchId())
                        .orElseThrow(() -> AppException.notFound("Branch", request.getBranchId()));
            }
        }

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> AppException.notFound("Employee (Manager)", request.getManagerId()));
        }

        Employee employee = modelMapper.map(request, Employee.class);
        employee.setEPassword(passwordEncoder.encode(request.getEPassword()));
        employee.setBranch(branch);
        employee.setManager(manager);
        employee.setActive(true);
        employee.setSubordinates(new HashSet<>());
        employee.setWorkShifts(new HashSet<>());
        if (request.getAvatarUrl() != null) {
            employee.setAvatarUrl(request.getAvatarUrl());
        }

        return toResponse(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponse update(String id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Employee", id));

        Branch targetBranch = employee.getBranch(); // Mặc định giữ nguyên chi nhánh cũ

        if (SecurityUtil.isManager()) {
            Integer managerBranchId = SecurityUtil.getCurrentBranchId();
            if (employee.getBranch() == null || !employee.getBranch().getBranchId().equals(managerBranchId)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Bạn không có quyền cập nhật nhân viên của chi nhánh khác");
            }
        } else if (SecurityUtil.isAdmin()) {
            if (request.getBranchId() != null && request.getBranchId() > 0) {
                targetBranch = branchRepository.findById(request.getBranchId())
                        .orElseThrow(() -> AppException.notFound("Branch", request.getBranchId()));
            } else {
                targetBranch = null;
            }
        }

        Employee manager = null;
        if (request.getManagerId() != null && !request.getManagerId().trim().isEmpty()) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> AppException.notFound("Employee (Manager)", request.getManagerId()));
        }

        String oldPassword = employee.getEPassword();

        modelMapper.map(request, employee);

        employee.setEUserId(id);

        if (request.getEPassword() != null && !request.getEPassword().isBlank()) {
            employee.setEPassword(passwordEncoder.encode(request.getEPassword()));
        } else {
            employee.setEPassword(oldPassword);
        }

        employee.setBranch(targetBranch);
        employee.setManager(manager);

        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
            employee.setAvatarUrl(request.getAvatarUrl());
        }

        return toResponse(employeeRepository.save(employee));
    }

    @Override
    public void deactivate(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Employee", id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    @Override
    public void activate(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Employee", id));
        employee.setActive(true);
        employeeRepository.save(employee);
    }

    @Override
    public void delete(String id) {
        if (!employeeRepository.existsById(id)) {
            throw AppException.notFound("Employee", id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public void assignWorkShifts(String employeeId, List<com.cms.dto.request.WorkShiftRequest> shiftRequests) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> AppException.notFound("Employee", employeeId));

        if (SecurityUtil.isManager()) {
            Integer managerBranchId = SecurityUtil.getCurrentBranchId();
            if (employee.getBranch() == null || !employee.getBranch().getBranchId().equals(managerBranchId)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Bạn không có quyền phân ca cho nhân viên chi nhánh khác");
            }
        }

        Set<WorkShift> assignedShifts = new HashSet<>();
        if (shiftRequests != null) {
            for (WorkShiftRequest req : shiftRequests) {
                WorkShiftId shiftId = new WorkShiftId(req.getStartTime(), req.getEndTime(), req.getWDate());

                WorkShift shift = workShiftRepository.findById(shiftId)
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                                String.format("Ca làm việc (Thứ %d, %s - %s) chưa được tạo trong hệ thống!",
                                        req.getWDate(), req.getStartTime(), req.getEndTime())));

                assignedShifts.add(shift);
            }
        }

        employee.setWorkShifts(assignedShifts);
        employeeRepository.save(employee);
    }

    @Override
    public void unassignWorkShift(String employeeId, java.time.LocalTime startTime, java.time.LocalTime endTime, Integer wDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> AppException.notFound("Employee", employeeId));

        if (SecurityUtil.isManager()) {
            Integer managerBranchId = SecurityUtil.getCurrentBranchId();
            if (employee.getBranch() == null || !employee.getBranch().getBranchId().equals(managerBranchId)) {
                throw new AppException(HttpStatus.UNAUTHORIZED, "Bạn không có quyền gỡ ca làm của nhân viên chi nhánh khác");
            }
        }

        WorkShiftId shiftId = new WorkShiftId(startTime, endTime, wDate);
        WorkShift shiftToRemove = workShiftRepository.findById(shiftId)
                .orElseThrow(() -> AppException.notFound("WorkShift", "PK: " + startTime + "-" + endTime + "-" + wDate));

        if (employee.getWorkShifts().contains(shiftToRemove)) {
            employee.getWorkShifts().remove(shiftToRemove);
            employeeRepository.save(employee);
        } else {
            throw new AppException(HttpStatus.BAD_REQUEST, "Nhân viên này hiện không có làm ca này, không thể gỡ!");
        }
    }
}
