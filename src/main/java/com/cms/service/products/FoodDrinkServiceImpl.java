package com.cms.service.products;

import com.cms.common.exception.AppException;
import com.cms.dto.request.FoodDrinkRequest;
import com.cms.dto.response.FoodDrinkResponse;
import com.cms.entity.products.FoodDrink;
import com.cms.repository.products.FoodDrinkRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodDrinkServiceImpl implements FoodDrinkService {

    private final FoodDrinkRepository foodDrinkRepository;
    private final ModelMapper modelMapper;

    private FoodDrinkResponse toResponse(FoodDrink fd) {
        return modelMapper.map(fd, FoodDrinkResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("food_drinks")
    public List<FoodDrinkResponse> getAll() {
        return foodDrinkRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "food_drinks", key = "#id")
    public FoodDrinkResponse getById(Integer id) {
        return toResponse(foodDrinkRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("FoodDrink", id)));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "food_drinks_type", key = "#pType")
    public List<FoodDrinkResponse> getByType(String pType) {
        return foodDrinkRepository.findByPType(pType).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodDrinkResponse> searchByName(String name) {
        return foodDrinkRepository.findByPNameContainingIgnoreCase(name).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "food_drinks", allEntries = true),
            @CacheEvict(value = "food_drinks_type", allEntries = true)
    })
    public FoodDrinkResponse create(FoodDrinkRequest request) {
        FoodDrink fd = modelMapper.map(request, FoodDrink.class);
        fd.setItemType("FOOD_DRINK");
        return toResponse(foodDrinkRepository.save(fd));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "food_drinks", allEntries = true),
            @CacheEvict(value = "food_drinks_type", allEntries = true)
    })
    public FoodDrinkResponse update(Integer id, FoodDrinkRequest request) {
        FoodDrink fd = foodDrinkRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("FoodDrink", id));
        modelMapper.map(request, fd);
        fd.setProductId(id);
        return toResponse(foodDrinkRepository.save(fd));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "food_drinks", allEntries = true),
            @CacheEvict(value = "food_drinks_type", allEntries = true)
    })
    public void delete(Integer id) {
        if (!foodDrinkRepository.existsById(id)) {
            throw AppException.notFound("FoodDrink", id);
        }
        foodDrinkRepository.deleteById(id);
    }
}
