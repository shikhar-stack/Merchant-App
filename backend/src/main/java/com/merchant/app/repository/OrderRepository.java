package com.merchant.app.repository;

import com.merchant.app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMerchantId(Long merchantId);

    List<Order> findByCropFarmerId(Long farmerId);
}
