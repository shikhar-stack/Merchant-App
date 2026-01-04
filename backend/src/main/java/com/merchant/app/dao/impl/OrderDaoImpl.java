package com.merchant.app.dao.impl;

import com.merchant.app.dao.OrderDao;
import com.merchant.app.entity.Order;
import com.merchant.app.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderDaoImpl implements OrderDao {

    private final OrderRepository orderRepository;

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findByMerchantId(Long merchantId) {
        return orderRepository.findByMerchantId(merchantId);
    }

    @Override
    public List<Order> findByCropFarmerId(Long farmerId) {
        return orderRepository.findByCropFarmerId(farmerId);
    }
}
