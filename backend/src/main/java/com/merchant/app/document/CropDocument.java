package com.merchant.app.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Document(indexName = "crops")
public class CropDocument {
    @Id
    private String id;
    private String name;
    private BigDecimal pricePerKg;
    private Double quantityAvailable;
    private Long farmerId;
    private String farmerName;
}
