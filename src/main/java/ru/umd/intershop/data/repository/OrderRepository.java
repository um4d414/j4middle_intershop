package ru.umd.intershop.data.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.umd.intershop.common.constant.OrderStatusEnum;
import ru.umd.intershop.data.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @EntityGraph(attributePaths = "items")
    Optional<OrderEntity> findById(@NonNull Long id);

    @Override
    void delete(@NonNull OrderEntity entity);

    Optional<OrderEntity> findLastByStatus(OrderStatusEnum status);

    List<OrderEntity> findAllByStatus(OrderStatusEnum status);
}
