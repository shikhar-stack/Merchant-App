package com.merchant.app.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CropDto {
    private Long id;
    private String name;
    private BigDecimal pricePerKg;
    private Double quantityAvailable;
    private Long farmerId;
    private String farmerName;
}
