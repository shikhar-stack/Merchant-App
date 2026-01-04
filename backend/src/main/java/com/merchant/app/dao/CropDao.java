package com.merchant.app.dao;

import com.merchant.app.entity.Crop;
import java.util.List;
import java.util.Optional;

public interface CropDao {
    Crop save(Crop crop);

    Optional<Crop> findById(Long id);

    List<Crop> findByFarmerId(Long farmerId);
}
