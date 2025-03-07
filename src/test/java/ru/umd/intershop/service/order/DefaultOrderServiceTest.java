package ru.umd.intershop.service.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.umd.intershop.common.constant.CartItemAction;
import ru.umd.intershop.common.constant.OrderStatusEnum;
import ru.umd.intershop.data.entity.*;
import ru.umd.intershop.data.repository.*;
import ru.umd.intershop.service.dto.OrderDto;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class DefaultOrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderService orderService; // Это DefaultOrderService

    @BeforeEach
    public void setUp() {
        // Очистка таблиц перед каждым тестом
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void testFindByIdFound() {
        // Создаем заказ без товаров
        OrderEntity order = OrderEntity.builder()
            .status(OrderStatusEnum.COMPLETED)
            .totalPrice(new BigDecimal("50.00"))
            .items(new ArrayList<>())
            .build();
        order = orderRepository.save(order);

        Optional<OrderDto> result = orderService.findById(order.getId());
        assertTrue(result.isPresent(), "Заказ должен быть найден");
        OrderDto dto = result.get();
        assertEquals(order.getId(), dto.getId());
        assertEquals(order.getTotalPrice(), dto.getTotalPrice());
        // Список товаров может быть пустым
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    public void testFindAllCompleted() {
        OrderEntity order1 = OrderEntity.builder()
            .status(OrderStatusEnum.COMPLETED)
            .totalPrice(new BigDecimal("100.00"))
            .items(new ArrayList<>())
            .build();
        OrderEntity order2 = OrderEntity.builder()
            .status(OrderStatusEnum.COMPLETED)
            .totalPrice(new BigDecimal("200.00"))
            .items(new ArrayList<>())
            .build();
        OrderEntity order3 = OrderEntity.builder()
            .status(OrderStatusEnum.NEW)
            .totalPrice(new BigDecimal("300.00"))
            .items(new ArrayList<>())
            .build();
        orderRepository.saveAll(Arrays.asList(order1, order2, order3));

        List<OrderDto> completedOrders = orderService.findAllCompleted();
        assertEquals(2, completedOrders.size(), "Должно быть 2 завершённых заказа");
        completedOrders.forEach(dto ->
                                    assertEquals(OrderStatusEnum.COMPLETED, dto.getStatus())
        );
    }

    @Test
    public void testGetCartWhenNotExists() {
        // Если заказ с статусом NEW отсутствует, getCart должен создать новый
        OrderDto cartDto = orderService.getCart();
        assertNotNull(cartDto, "Корзина не должна быть null");
        assertEquals(OrderStatusEnum.NEW, cartDto.getStatus(), "Новый заказ должен иметь статус NEW");
        assertEquals(BigDecimal.ZERO, cartDto.getTotalPrice(), "Новый заказ должен иметь totalPrice = 0");
        assertTrue(cartDto.getItems().isEmpty(), "В новом заказе не должно быть товаров");
    }

    @Test
    public void testUpdateItemCount() {
        // Создаем товар
        ItemEntity item = ItemEntity.builder()
            .name("Test Item")
            .description("Test Description")
            .price(new BigDecimal("10.00"))
            .imageFileName("test.jpg")
            .isActive(true)
            .build();
        item = itemRepository.save(item);

        // Поскольку корзина создаётся автоматически, вызов updateItemCount с действием PLUS должен добавить товар
        orderService.updateItemCount(item.getId(), CartItemAction.PLUS);
        OrderEntity cart = orderRepository.findLastByStatus(OrderStatusEnum.NEW)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        assertEquals(1, cart.getItems().size(), "В корзине должен быть один товар");
        OrderItemEntity orderItem = cart.getItems().get(0);
        assertEquals(1, orderItem.getCount(), "Количество должно быть 1 после первого PLUS");

        // Вызовем MINUS — количество должно уменьшиться до 0 (но оставаться в корзине)
        orderService.updateItemCount(item.getId(), CartItemAction.MINUS);
        cart = orderRepository.findLastByStatus(OrderStatusEnum.NEW).orElseThrow();
        orderItem = cart.getItems().get(0);
        assertEquals(0, orderItem.getCount(), "Количество должно стать 0 после MINUS");

        // Вызов DELETE должен удалить товар из корзины
        orderService.updateItemCount(item.getId(), CartItemAction.DELETE);
        cart = orderRepository.findLastByStatus(OrderStatusEnum.NEW).orElseThrow();
        assertTrue(cart.getItems().isEmpty(), "После DELETE корзина должна быть пуста");
    }

    @Test
    public void testProcessCart() {
        // Создаем товар и добавляем его в корзину с помощью updateItemCount
        ItemEntity item = ItemEntity.builder()
            .name("Test Item")
            .description("Test Desc")
            .price(new BigDecimal("10.00"))
            .imageFileName("test.jpg")
            .isActive(true)
            .build();
        item = itemRepository.save(item);

        // Добавляем товар дважды (считаем, что каждый вызов PLUS увеличивает количество на 1)
        orderService.updateItemCount(item.getId(), CartItemAction.PLUS);
        orderService.updateItemCount(item.getId(), CartItemAction.PLUS);

        // Обработка корзины: вычисляется totalPrice и статус меняется на COMPLETED
        Long processedOrderId = orderService.processCart();
        OrderEntity processedOrder = orderRepository.findById(processedOrderId)
            .orElseThrow(() -> new RuntimeException("Processed order not found"));
        assertEquals(OrderStatusEnum.COMPLETED, processedOrder.getStatus(), "Статус заказа должен быть COMPLETED");
        // Ожидаем totalPrice = 10.00 * 2 = 20.00
        assertEquals(new BigDecimal("20.00"), processedOrder.getTotalPrice());
    }
}