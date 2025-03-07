package ru.umd.intershop.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.umd.intershop.common.constant.OrderStatusEnum;
import ru.umd.intershop.data.entity.ItemEntity;
import ru.umd.intershop.data.entity.OrderEntity;
import ru.umd.intershop.data.repository.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class ShopIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    public void setup() {
        // Очищаем данные перед каждым тестом
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();
    }

    /**
     * Полная цепочка: 1. Добавляем товар в БД. 2. Загружаем страницу с товарами (/main/items). 3. Добавляем товар в
     * корзину (POST /items/{id}?action=PLUS). 4. Проверяем, что в корзине отображается товар. 5. Оформляем заказ (POST
     * /buy). 6. Проверяем, что заказ обработан (статус COMPLETED, totalPrice рассчитан).
     */
    @Test
    public void testFullPurchaseFlow() throws Exception {
        // Шаг 1. Создаём и сохраняем тестовый товар
        ItemEntity item = ItemEntity.builder()
            .name("Test Item")
            .description("Description for Test Item")
            .price(new BigDecimal("100.00"))
            .imageFileName("test.jpg")
            .isActive(true)
            .build();
        item = itemRepository.save(item);

        // Шаг 2. Загружаем страницу товаров
        mockMvc.perform(get("/main/items")
                            .param("pageSize", "10")
                            .param("pageNumber", "0")
                            .param("sort", "NO")
                            .param("search", "Test"))
            .andExpect(status().isOk())
            .andExpect(view().name("main"))
            .andExpect(model().attributeExists("paging"))
            .andExpect(model().attributeExists("items"));

        // Шаг 3. Добавляем товар в корзину (действие PLUS)
        mockMvc.perform(post("/items/" + item.getId())
                            .param("action", "PLUS"))
            .andExpect(status().is3xxRedirection());

        // Шаг 4. Проверяем страницу корзины
        mockMvc.perform(get("/cart/items"))
            .andExpect(status().isOk())
            .andExpect(view().name("cart"))
            .andExpect(model().attribute("empty", is(false)))
            .andExpect(model().attribute("items", hasSize(1)))
            .andExpect(model().attribute("total", equalTo(new BigDecimal("100.00"))));

        // Шаг 5. Оформляем заказ
        mockMvc.perform(post("/buy"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/orders/*?isNew=true"));

        // Шаг 6. Проверяем, что заказ обработан (статус COMPLETED, totalPrice рассчитан)
        Optional<OrderEntity> processedOrderOpt = orderRepository.findAll().stream()
            .filter(order -> order.getStatus() == OrderStatusEnum.COMPLETED)
            .findFirst();
        if (processedOrderOpt.isEmpty()) {
            throw new RuntimeException("Processed order not found");
        }
        OrderEntity processedOrder = processedOrderOpt.get();
        assertEquals(new BigDecimal("100.00"), processedOrder.getTotalPrice(), "TotalPrice должен быть 100.00");
    }

    /**
     * Сценарий с добавлением нескольких товаров, удалением одного и оформлением заказа.
     */
    @Test
    public void testMultipleItemsAndDeleteFlow() throws Exception {
        // Шаг 1. Создаём два товара
        ItemEntity item1 = ItemEntity.builder()
            .name("Item 1")
            .description("Desc 1")
            .price(new BigDecimal("50.00"))
            .imageFileName("item1.jpg")
            .isActive(true)
            .build();
        ItemEntity item2 = ItemEntity.builder()
            .name("Item 2")
            .description("Desc 2")
            .price(new BigDecimal("150.00"))
            .imageFileName("item2.jpg")
            .isActive(true)
            .build();
        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        // Шаг 2. Добавляем товар item1 дважды и item2 один раз
        mockMvc.perform(post("/items/" + item1.getId())
                            .param("action", "PLUS"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/items/" + item1.getId())
                            .param("action", "PLUS"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/items/" + item2.getId())
                            .param("action", "PLUS"))
            .andExpect(status().is3xxRedirection());

        // Шаг 3. Удаляем товар item1
        mockMvc.perform(post("/items/" + item1.getId())
                            .param("action", "DELETE"))
            .andExpect(status().is3xxRedirection());

        // Шаг 4. Проверяем корзину: ожидается, что остался только item2 с count = 1
        mockMvc.perform(get("/cart/items"))
            .andExpect(status().isOk())
            .andExpect(view().name("cart"))
            .andExpect(model().attribute("empty", is(false)))
            .andExpect(model().attribute("items", hasSize(1)))
            .andExpect(model().attribute("total", equalTo(new BigDecimal("150.00"))));

        // Шаг 5. Оформляем заказ
        mockMvc.perform(post("/buy"))
            .andExpect(status().is3xxRedirection());

        // Шаг 6. Проверяем, что заказ обработан корректно
        Optional<OrderEntity> processedOrderOpt = orderRepository.findAll().stream()
            .filter(order -> order.getStatus() == OrderStatusEnum.COMPLETED)
            .findFirst();
        if (processedOrderOpt.isEmpty()) {
            throw new RuntimeException("Processed order not found");
        }
        OrderEntity processedOrder = processedOrderOpt.get();
        assertEquals(new BigDecimal("150.00"), processedOrder.getTotalPrice(), "TotalPrice должен быть 150.00");
    }
}

