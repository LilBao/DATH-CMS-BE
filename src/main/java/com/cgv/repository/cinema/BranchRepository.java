package com.cgv.repository.cinema;

import com.cgv.entity.cinema.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    List<Branch> findByBNameContainingIgnoreCase(String name);
}
