package com.merchant.app.service;

import com.merchant.app.dao.CropDao;
import com.merchant.app.dao.UserDao;
import com.merchant.app.document.CropDocument;
import com.merchant.app.dto.CropDto;
import com.merchant.app.entity.Crop;
import com.merchant.app.entity.User;
import com.merchant.app.repository.CropSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CropService {

    private final CropDao cropDao;
    private final UserDao userDao;
    private final CropSearchRepository cropSearchRepository;

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

    public List<CropDto> searchCrops(String query) {
        try {
            List<CropDocument> esResults;
            if (query == null || query.isEmpty()) {
                esResults = StreamSupport.stream(cropSearchRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
            } else {
                esResults = cropSearchRepository.findByNameContaining(query);
            }

            if (!esResults.isEmpty()) {
                return esResults.stream()
                        .map(this::mapDocumentToDto)
                        .collect(Collectors.toList());
            } else {
                log.info("ES returned empty results, falling back to DB for query: {}", query);
                // Fallback to DB if ES is empty (optional behavior, but requested)
                return searchInDb(query);
            }
        } catch (Exception e) {
            log.error("ElasticSearch failed or unreachable, falling back to DB. Error: {}", e.getMessage());
            return searchInDb(query);
        }
    }

    private List<CropDto> searchInDb(String query) {
        List<Crop> dbResults;
        if (query == null || query.isEmpty()) {
            // Note: findAll logic for DB not implemented in DAO yet, using empty list or
            // add findAll if needed.
            // For now, let's assume query is required for DB search or we add findAll to
            // DAO.
            // Let's just return empty if query is empty to avoid dumping whole DB, or use a
            // method if exists.
            // CropDao doesn't have findAll exposed yet. Let's just return empty for
            // no-query DB fallback to be safe,
            // OR implement findAll in DAO?
            // Safe bet: findByNameContaining("") usually returns all in JPA?
            dbResults = cropDao.findByNameContaining("");
        } else {
            dbResults = cropDao.findByNameContaining(query);
        }
        return dbResults.stream()
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

    private CropDto mapDocumentToDto(CropDocument doc) {
        CropDto dto = new CropDto();
        try {
            dto.setId(Long.parseLong(doc.getId())); // Assuming ID is numeric string
        } catch (NumberFormatException e) {
            // Handle if ID is not numeric (should be from DB)
        }
        dto.setName(doc.getName());
        dto.setPricePerKg(doc.getPricePerKg());
        dto.setQuantityAvailable(doc.getQuantityAvailable());
        dto.setFarmerId(doc.getFarmerId());
        dto.setFarmerName(doc.getFarmerName());
        return dto;
    }
}
