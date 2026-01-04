package com.merchant.app.dao;

import com.merchant.app.entity.Order;
import java.util.List;

public interface OrderDao {
    Order save(Order order);

    List<Order> findByMerchantId(Long merchantId);

    List<Order> findByCropFarmerId(Long farmerId);
}
