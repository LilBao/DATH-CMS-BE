package com.cms.repository.cinema;

import com.cms.entity.cinema.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    List<Branch> findByBNameContainingIgnoreCase(String name);
}
