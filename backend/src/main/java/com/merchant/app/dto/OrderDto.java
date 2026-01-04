package com.merchant.app.dto;

import com.merchant.app.entity.Order.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private Long cropId;
    private String cropName;
    private Long merchantId;
    private String merchantName;
    private Double quantityOrdered;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime orderDate;
}
