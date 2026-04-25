package com.cms.repository.products;

import com.cms.entity.products.Merchandise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Integer> {
    List<Merchandise> findByMerchNameContainingIgnoreCase(String name);
}
