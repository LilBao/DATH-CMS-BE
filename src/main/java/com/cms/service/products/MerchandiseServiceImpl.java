package com.cms.service.products;

import com.cms.common.exception.AppException;
import com.cms.dto.request.MerchandiseRequest;
import com.cms.dto.response.MerchandiseResponse;
import com.cms.entity.products.Merchandise;
import com.cms.repository.products.MerchandiseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MerchandiseServiceImpl implements MerchandiseService {

    private final MerchandiseRepository merchandiseRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("merchandise")
    public List<MerchandiseResponse> getAll() {
        return merchandiseRepository.findAll().stream()
                .map(m -> modelMapper.map(m, MerchandiseResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "merchandise", key = "#id")
    public MerchandiseResponse getById(Integer id) {
        Merchandise merchandise = merchandiseRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Merchandise", id.toString()));
        return modelMapper.map(merchandise, MerchandiseResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchandiseResponse> searchByName(String name) {
        return merchandiseRepository.findByMerchNameContainingIgnoreCase(name).stream()
                .map(m -> modelMapper.map(m, MerchandiseResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "merchandise", allEntries = true)
    public MerchandiseResponse create(MerchandiseRequest request) {
        Merchandise merchandise = modelMapper.map(request, Merchandise.class);
        merchandise.setItemType("MERCHANDISE");
        return modelMapper.map(merchandiseRepository.save(merchandise), MerchandiseResponse.class);
    }

    @Override
    @CacheEvict(value = "merchandise", allEntries = true)
    public MerchandiseResponse update(Integer id, MerchandiseRequest request) {
        Merchandise merchandise = merchandiseRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Merchandise", id.toString()));
        modelMapper.map(request, merchandise);
        merchandise.setProductId(id); // Ensure ID is not changed
        return modelMapper.map(merchandiseRepository.save(merchandise), MerchandiseResponse.class);
    }

    @Override
    @CacheEvict(value = "merchandise", allEntries = true)
    public void delete(Integer id) {
        if (!merchandiseRepository.existsById(id)) {
            throw AppException.notFound("Merchandise", id.toString());
        }
        merchandiseRepository.deleteById(id);
    }
}
