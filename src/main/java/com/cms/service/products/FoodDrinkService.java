package com.cms.service.products;

import com.cms.dto.request.FoodDrinkRequest;
import com.cms.dto.response.FoodDrinkResponse;

import java.util.List;

public interface FoodDrinkService {
    List<FoodDrinkResponse> getAll();
    FoodDrinkResponse getById(Integer id);
    List<FoodDrinkResponse> getByType(String pType);
    List<FoodDrinkResponse> searchByName(String name);
    FoodDrinkResponse create(FoodDrinkRequest request);
    FoodDrinkResponse update(Integer id, FoodDrinkRequest request);
    void delete(Integer id);
}
