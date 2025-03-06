package ru.umd.intershop.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.umd.intershop.common.constant.ItemCountAction;
import ru.umd.intershop.common.constant.OrderStatusEnum;
import ru.umd.intershop.data.entity.OrderEntity;
import ru.umd.intershop.data.entity.OrderItemEntity;
import ru.umd.intershop.data.repository.*;
import ru.umd.intershop.service.dto.OrderDto;
import ru.umd.intershop.service.order.mapper.OrderServiceMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {
    private final OrderRepository orderRepository;

    private final ItemRepository itemRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderServiceMapper orderServiceMapper;

    @Override
    public Optional<OrderDto> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<OrderDto> findAll() {
        return orderRepository
            .findAll()
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
    public void updateItemCount(Long id, ItemCountAction action) {
        var cart = getCartEntity();

        var orderItem = cart.getItems()
            .stream()
            .filter(orderItemEntity -> orderItemEntity.getItem().getId().equals(id))
            .findFirst()
            .orElseGet(() -> {
                var itemEntity = itemRepository
                    .findById(id)
                    .orElseThrow(() -> new RuntimeException("Item not found"));

                return orderItemRepository.save(
                    OrderItemEntity
                        .builder()
                        .count(0)
                        .item(itemEntity)
                        .order(cart)
                        .build()
                );
            });

        orderItem.setCount(
            ItemCountAction.PLUS.equals(action)
                ? orderItem.getCount() + 1
                : orderItem.getCount() - 1
        );

        if (orderItem.getCount() < 0) {
            orderItem.setCount(0);
        }

        orderItemRepository.save(orderItem);
    }

    private OrderEntity getCartEntity() {
        return orderRepository
            .findLastByStatus(OrderStatusEnum.NEW)
            .orElseGet(() -> {
                var entity = new OrderEntity();
                entity.setStatus(OrderStatusEnum.NEW);
                entity.setTotalPrice(BigDecimal.ZERO);
                entity.setItems(List.of());

                return orderRepository.save(entity);
            });
    }
}
