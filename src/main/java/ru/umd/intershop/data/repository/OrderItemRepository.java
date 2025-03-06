package ru.umd.intershop.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umd.intershop.data.entity.OrderItemEntity;
import ru.umd.intershop.data.entity.embedded.OrderItemId;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, OrderItemId> {
}
