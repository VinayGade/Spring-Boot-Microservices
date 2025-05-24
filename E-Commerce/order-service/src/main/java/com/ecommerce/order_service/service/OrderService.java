package com.ecommerce.order_service.service;

import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.ecommerce.order_service.client.InventoryClient.log;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest){

        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if(isProductInStock){
            //map order request to Order object
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            //save order to order repository
            orderRepository.save(order);

            // send the message to kafka Topic:
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                    order.getOrderNumber(), orderRequest.userDetails().email());

            log.info("Start sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);

            kafkaTemplate.send("order-placed", orderPlacedEvent);

            log.info("End sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
        }else{
            throw new RuntimeException("Product with Sku Code "+orderRequest.skuCode() + " is not in stock...");
        }
    }

    public List<Order> findAll(){
        return orderRepository.findAll();
    }
}
