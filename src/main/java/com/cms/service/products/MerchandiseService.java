package com.cms.service.products;

import com.cms.dto.request.MerchandiseRequest;
import com.cms.dto.response.MerchandiseResponse;

import java.util.List;

public interface MerchandiseService {
    List<MerchandiseResponse> getAll();
    MerchandiseResponse getById(Integer id);
    List<MerchandiseResponse> searchByName(String name);
    MerchandiseResponse create(MerchandiseRequest request);
    MerchandiseResponse update(Integer id, MerchandiseRequest request);
    void delete(Integer id);
}
