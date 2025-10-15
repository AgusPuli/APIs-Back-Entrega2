package com.uade.tpo.ecommerce.controllers.orders;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.uade.tpo.ecommerce.controllers.orders.OrderResponses.*;

@Component
public class OrderMapper {

    public OrderSummaryResponse toSummary(Order o) {
        if (o == null) return null;
        return new OrderSummaryResponse(
                o.getId(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getTotal()
        );
    }

    public OrderDetailResponse toDetail(Order o) {
        if (o == null) return null;
        List<OrderItemResponse> items = (o.getItems() == null) ? List.of()
                : o.getItems().stream().map(this::toItem).toList();

        return new OrderDetailResponse(
                o.getId(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getTotal(),
                items
        );
    }

    private OrderItemResponse toItem(OrderItem i) {
        Long productId = (i.getProduct() != null) ? i.getProduct().getId() : null;
        String name    = (i.getProduct() != null) ? i.getProduct().getName() : null;
        return new OrderItemResponse(productId, name, i.getUnitPrice(), i.getQuantity());
    }
}
