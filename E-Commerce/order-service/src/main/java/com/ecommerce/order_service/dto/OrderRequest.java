package com.ecommerce.order_service.dto;

import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderRequest(Long id, String orderNumber, String skuCode, BigDecimal price, Integer quantity, UserDetails userDetails) {
    public record UserDetails(String email, String firstName, String lastName) {}
}
