package com.study.orderservice.repository;

import com.study.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserId(Long id);

    Optional<Order> findByIdAndDeletedFalse(Long id);

    List<Order> findByUserIdAndDeletedFalse(Long userId);
}

