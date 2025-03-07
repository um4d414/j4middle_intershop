package ru.umd.intershop.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.umd.intershop.service.dto.*;
import ru.umd.intershop.service.order.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    // Тест для GET /orders, который возвращает список завершённых заказов
    @Test
    public void testOrders() throws Exception {
        // Создаем тестовый ItemDto
        ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Test Item")
            .description("Test Description")
            .price(new BigDecimal("10.00"))
            .imageFileName("test.jpg")
            .build();

        // Создаем тестовый OrderItemDto
        OrderItemDto orderItemDto = OrderItemDto.builder()
            .item(itemDto)
            .count(2)
            .build();

        // Создаем тестовый OrderDto
        OrderDto orderDto = OrderDto.builder()
            .id(100L)
            .totalPrice(new BigDecimal("20.00"))
            .items(List.of(orderItemDto))
            .build();

        when(orderService.findAllCompleted()).thenReturn(List.of(orderDto));

        mockMvc.perform(get("/orders"))
            .andExpect(status().isOk())
            .andExpect(view().name("orders"))
            .andExpect(model().attributeExists("orders"))
            .andExpect(model().attribute("orders", hasSize(1)));
    }

    // Тест для GET /orders/{id}
    @Test
    public void testOrder() throws Exception {
        // Создаем тестовый ItemDto
        ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Test Item")
            .description("Test Description")
            .price(new BigDecimal("10.00"))
            .imageFileName("test.jpg")
            .build();

        // Создаем тестовый OrderItemDto
        OrderItemDto orderItemDto = OrderItemDto.builder()
            .item(itemDto)
            .count(2)
            .build();

        // Создаем тестовый OrderDto
        OrderDto orderDto = OrderDto.builder()
            .id(100L)
            .totalPrice(new BigDecimal("20.00"))
            .items(List.of(orderItemDto))
            .build();

        when(orderService.findById(100L)).thenReturn(Optional.of(orderDto));

        mockMvc.perform(get("/orders/100")
                            .param("isNew", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("order"))
            .andExpect(model().attributeExists("order"))
            .andExpect(model().attributeExists("newOrder"))
            .andExpect(model().attribute("newOrder", true));
    }

    // Тест для GET /cart/items
    @Test
    public void testCart() throws Exception {
        // Создаем тестовый ItemDto
        ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Test Item")
            .description("Test Description")
            .price(new BigDecimal("10.00"))
            .imageFileName("test.jpg")
            .build();

        // Создаем тестовый OrderItemDto
        OrderItemDto orderItemDto = OrderItemDto.builder()
            .item(itemDto)
            .count(2)
            .build();

        // Создаем тестовый OrderDto, который представляет корзину
        OrderDto cartDto = OrderDto.builder()
            .id(200L)
            .totalPrice(new BigDecimal("20.00"))
            .items(List.of(orderItemDto))
            .build();

        when(orderService.getCart()).thenReturn(cartDto);

        mockMvc.perform(get("/cart/items"))
            .andExpect(status().isOk())
            .andExpect(view().name("cart"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("empty"))
            .andExpect(model().attributeExists("total"));
    }

    // Тест для POST /buy
    @Test
    public void testProcessOrder() throws Exception {
        when(orderService.processCart()).thenReturn(300L);

        mockMvc.perform(post("/buy"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/orders/300?isNew=true"));
    }
}