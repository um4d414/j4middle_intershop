package ru.umd.intershop.service.order.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.umd.intershop.data.entity.OrderEntity;
import ru.umd.intershop.service.dto.*;
import ru.umd.intershop.service.product.mapper.ItemServiceMapper;

@Component
@RequiredArgsConstructor
public class OrderServiceMapper {
    private final ItemServiceMapper itemServiceMapper;

    public OrderDto map(OrderEntity orderEntity) {
        return OrderDto
            .builder()
            .id(orderEntity.getId())
            .totalPrice(orderEntity.getTotalPrice())
            .items(
                orderEntity
                    .getItems()
                    .stream()
                    .map(
                        orderItemEntity -> OrderItemDto
                            .builder()
                            .item(itemServiceMapper.map(orderItemEntity.getItem()))
                            .count(orderItemEntity.getCount())
                            .build()
                    )
                    .toList()
            )
            .build();
    }
}
