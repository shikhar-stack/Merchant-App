package com.merchant.app.dao.impl;

import com.merchant.app.dao.CropDao;
import com.merchant.app.entity.Crop;
import com.merchant.app.repository.CropRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CropDaoImpl implements CropDao {

    private final CropRepository cropRepository;

    @Override
    public Crop save(Crop crop) {
        return cropRepository.save(crop);
    }

    @Override
    public Optional<Crop> findById(Long id) {
        return cropRepository.findById(id);
    }

    @Override
    public List<Crop> findByFarmerId(Long farmerId) {
        return cropRepository.findByFarmerId(farmerId);
    }
}
