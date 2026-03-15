package com.study.orderservice.specification;

import com.study.orderservice.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> createdAfter(LocalDateTime from) {
        return (root, query, cb) ->
                from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Order> createdBefore(LocalDateTime to) {
        return (root, query, cb) ->
                to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<Order> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Order> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

}
