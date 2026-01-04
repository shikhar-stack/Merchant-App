package com.merchant.app.service;

import com.merchant.app.dao.CropDao;
import com.merchant.app.dao.OrderDao;
import com.merchant.app.dao.UserDao;
import com.merchant.app.dto.OrderDto;
import com.merchant.app.entity.Crop;
import com.merchant.app.entity.Order;
import com.merchant.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDao orderDao;
    private final CropDao cropDao;
    private final UserDao userDao;

    @Transactional
    public OrderDto placeOrder(Long cropId, Double quantity, String merchantEmail) {
        User merchant = userDao.findByEmail(merchantEmail)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (merchant.getRole() != User.Role.MERCHANT) {
            throw new RuntimeException("Only merchants can place orders");
        }

        Crop crop = cropDao.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        if (crop.getQuantityAvailable() < quantity) {
            throw new RuntimeException("Insufficient quantity available");
        }

        // Decrement quantity
        crop.setQuantityAvailable(crop.getQuantityAvailable() - quantity);
        cropDao.save(crop);

        Order order = new Order();
        order.setCrop(crop);
        order.setMerchant(merchant);
        order.setQuantityOrdered(quantity);
        order.setTotalPrice(crop.getPricePerKg().multiply(BigDecimal.valueOf(quantity)));
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderDao.save(order);
        return mapToDto(savedOrder);
    }

    public List<OrderDto> getOrdersForMerchant(String merchantEmail) {
        User merchant = userDao.findByEmail(merchantEmail)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        return orderDao.findByMerchantId(merchant.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersForFarmer(String farmerEmail) {
        User farmer = userDao.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return orderDao.findByCropFarmerId(farmer.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCropId(order.getCrop().getId());
        dto.setCropName(order.getCrop().getName());
        dto.setMerchantId(order.getMerchant().getId());
        dto.setMerchantName(order.getMerchant().getName());
        dto.setQuantityOrdered(order.getQuantityOrdered());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        return dto;
    }
}
