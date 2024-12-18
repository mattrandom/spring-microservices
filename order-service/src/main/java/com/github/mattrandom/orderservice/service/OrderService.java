package com.github.mattrandom.orderservice.service;

import com.github.mattrandom.orderservice.dto.OrderRequest;
import com.github.mattrandom.orderservice.dto.OrderResponse;
import com.github.mattrandom.orderservice.model.Order;
import com.github.mattrandom.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .withOrderNumber(UUID.randomUUID().toString())
                .withSkuCode(orderRequest.skuCode())
                .withPrice(orderRequest.price())
                .withQuantity(orderRequest.quantity())
                .build();

        orderRepository.save(order);

        return mapToOrderResponse(order);
    }

    public OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getSkuCode(), order.getPrice(), order.getQuantity());
    }
}
