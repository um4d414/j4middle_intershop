package ru.umd.intershop.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.umd.intershop.data.repository.OrderRepository;
import ru.umd.intershop.service.dto.OrderDto;
import ru.umd.intershop.service.order.mapper.OrderServiceMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {
    private final OrderRepository orderRepository;

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
}
