package com.cms.repository.products;

import com.cms.entity.products.FoodDrink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodDrinkRepository extends JpaRepository<FoodDrink, Integer> {
    List<FoodDrink> findByPType(String pType);
    List<FoodDrink> findByPNameContainingIgnoreCase(String name);
}
