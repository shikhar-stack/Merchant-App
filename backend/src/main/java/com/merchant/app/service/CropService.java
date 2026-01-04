package com.merchant.app.service;

import com.merchant.app.dao.CropDao;
import com.merchant.app.dao.UserDao;
import com.merchant.app.dto.CropDto;
import com.merchant.app.entity.Crop;
import com.merchant.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CropService {

    private final CropDao cropDao;
    private final UserDao userDao;

    public CropDto addCrop(CropDto cropDto, String farmerEmail) {
        User farmer = userDao.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        if (farmer.getRole() != User.Role.FARMER) {
            throw new RuntimeException("Only farmers can add crops");
        }

        Crop crop = new Crop();
        crop.setName(cropDto.getName());
        crop.setPricePerKg(cropDto.getPricePerKg());
        crop.setQuantityAvailable(cropDto.getQuantityAvailable());
        crop.setFarmer(farmer);

        Crop savedCrop = cropDao.save(crop);
        return mapToDto(savedCrop);
    }

    public List<CropDto> getCropsByFarmer(String farmerEmail) {
        User farmer = userDao.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        return cropDao.findByFarmerId(farmer.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CropDto mapToDto(Crop crop) {
        CropDto dto = new CropDto();
        dto.setId(crop.getId());
        dto.setName(crop.getName());
        dto.setPricePerKg(crop.getPricePerKg());
        dto.setQuantityAvailable(crop.getQuantityAvailable());
        dto.setFarmerId(crop.getFarmer().getId());
        dto.setFarmerName(crop.getFarmer().getName());
        return dto;
    }
}
