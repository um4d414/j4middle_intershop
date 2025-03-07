package ru.umd.intershop.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.umd.intershop.common.constant.CartItemAction;
import ru.umd.intershop.common.constant.OrderStatusEnum;
import ru.umd.intershop.data.entity.OrderEntity;
import ru.umd.intershop.data.entity.OrderItemEntity;
import ru.umd.intershop.data.repository.*;
import ru.umd.intershop.service.dto.OrderDto;
import ru.umd.intershop.service.order.mapper.OrderServiceMapper;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {
    private final OrderRepository orderRepository;

    private final ItemRepository itemRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderServiceMapper orderServiceMapper;

    @Override
    public Optional<OrderDto> findById(Long id) {
        return orderRepository
            .findById(id)
            .map(orderServiceMapper::map);
    }

    @Override
    public List<OrderDto> findAllCompleted() {
        return orderRepository
            .findAllByStatus(OrderStatusEnum.COMPLETED)
            .stream()
            .map(orderServiceMapper::map)
            .toList();
    }

    @Override
    public OrderDto getCart() {
        var cart = getCartEntity();

        return orderServiceMapper.map(cart);
    }

    @Override
    @Transactional
    public void updateItemCount(Long id, CartItemAction action) {
        var cart = getCartEntity();

        var orderItem = cart.getItems()
            .stream()
            .filter(orderItemEntity -> orderItemEntity.getItem().getId().equals(id))
            .findFirst()
            .orElseGet(() -> {
                var itemEntity = itemRepository
                    .findById(id)
                    .orElseThrow(() -> new RuntimeException("Item not found"));

                var orderItemEntity = orderItemRepository.save(
                    OrderItemEntity
                        .builder()
                        .count(0)
                        .item(itemEntity)
                        .order(cart)
                        .build()
                );

                cart.getItems().add(orderItemEntity);
                return orderItemEntity;
            });

        switch (action) {
            case PLUS -> orderItem.setCount(orderItem.getCount() + 1);
            case MINUS -> orderItem.setCount(orderItem.getCount() - 1);
            case DELETE -> {
                cart.getItems().remove(orderItem);
                orderItemRepository.deleteById(orderItem.getId());
                return;
            }
        }

        if (orderItem.getCount() <= 0) {
            orderItem.setCount(0);
        }

        orderItemRepository.save(orderItem);
    }

    @Override
    @Transactional
    public Long processCart() {
        var cartOrderEntity = orderRepository
            .findLastByStatus(OrderStatusEnum.NEW)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        cartOrderEntity.setTotalPrice(
            cartOrderEntity
                .getItems()
                .stream()
                .map(
                    orderItem -> orderItem.getItem().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getCount()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        cartOrderEntity.setStatus(OrderStatusEnum.COMPLETED);
        return orderRepository.save(cartOrderEntity).getId();
    }

    private OrderEntity getCartEntity() {
        return orderRepository
            .findLastByStatus(OrderStatusEnum.NEW)
            .orElseGet(() -> {
                var entity = new OrderEntity();
                entity.setStatus(OrderStatusEnum.NEW);
                entity.setTotalPrice(BigDecimal.ZERO);
                entity.setItems(new ArrayList<>());

                return orderRepository.save(entity);
            });
    }
}
