package com.cms.service.staff;

import com.cms.common.exception.AppException;
import com.cms.dto.request.EmployeeRequest;
import com.cms.dto.response.EmployeeResponse;
import com.cms.entity.cinema.Branch;
import com.cms.entity.staff.Employee;
import com.cms.repository.cinema.BranchRepository;
import com.cms.repository.staff.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
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

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> AppException.notFound("Branch", request.getBranchId()));

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

        return toResponse(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponse update(String id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Employee", id));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> AppException.notFound("Employee (Manager)", request.getManagerId()));
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> AppException.notFound("Branch", request.getBranchId()));

        modelMapper.map(request, employee);
        employee.setEUserId(id);
        if (request.getEPassword() != null && !request.getEPassword().isBlank()) {
            employee.setEPassword(passwordEncoder.encode(request.getEPassword()));
        }
        employee.setBranch(branch);
        employee.setManager(manager);

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
    public void delete(String id) {
        if (!employeeRepository.existsById(id)) {
            throw AppException.notFound("Employee", id);
        }
        employeeRepository.deleteById(id);
    }
}
