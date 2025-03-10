package ru.umd.intershop.service.order;

import ru.umd.intershop.common.constant.CartItemAction;
import ru.umd.intershop.service.dto.OrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<OrderDto> findById(Long id);

    List<OrderDto> findAllCompleted();

    OrderDto getCart();

    void updateItemCount(Long id, CartItemAction action);

    Long processCart();
}
