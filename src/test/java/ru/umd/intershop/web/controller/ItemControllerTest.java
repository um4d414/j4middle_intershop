package ru.umd.intershop.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.umd.intershop.common.constant.CartItemAction;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.service.dto.ItemDto;
import ru.umd.intershop.service.dto.OrderDto;
import ru.umd.intershop.service.item.ItemService;
import ru.umd.intershop.service.order.OrderService;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private OrderService orderService;

    @Test
    void testMainPage() throws Exception {
        // Подготовка тестовых данных для страницы /main/items
        ItemDto item1 = ItemDto.builder()
            .id(1L)
            .name("Item 1")
            .description("Description 1")
            .price(new BigDecimal("10.00"))
            .imageFileName("1.jpg")
            .build();
        ItemDto item2 = ItemDto.builder()
            .id(2L)
            .name("Item 2")
            .description("Description 2")
            .price(new BigDecimal("20.00"))
            .imageFileName("2.jpg")
            .build();
        // Возвращаем страницу с двумя товарами
        Page<ItemDto> page = new PageImpl<>(Arrays.asList(item1, item2));
        when(itemService.findAllActive(any(Pageable.class), any(ItemSortingEnum.class), anyString()))
            .thenReturn(page);

        // Корзина без товаров
        OrderDto cartDto = OrderDto.builder()
            .id(100L)
            .totalPrice(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();
        when(orderService.getCart()).thenReturn(cartDto);

        mockMvc.perform(get("/main/items")
                            .param("pageSize", "10")
                            .param("pageNumber", "0")
                            .param("sort", "NO")
                            .param("search", "test"))
            .andExpect(status().isOk())
            .andExpect(view().name("main"))
            .andExpect(model().attributeExists("paging"))
            .andExpect(model().attributeExists("items"));
    }

    @Test
    void testItemPage() throws Exception {
        // Подготовка тестовых данных для страницы товара /items/{id}
        ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item 1")
            .description("Description 1")
            .price(new BigDecimal("10.00"))
            .imageFileName("1.jpg")
            .build();
        when(itemService.findById(1L)).thenReturn(Optional.of(itemDto));

        // Корзина без товаров
        OrderDto cartDto = OrderDto.builder()
            .id(100L)
            .totalPrice(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();
        when(orderService.getCart()).thenReturn(cartDto);

        mockMvc.perform(get("/items/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("item"))
            .andExpect(model().attributeExists("item"));
    }

    @Test
    void testUpdateCartItem() throws Exception {
        doNothing().when(orderService).updateItemCount(eq(1L), any(CartItemAction.class));

        mockMvc.perform(post("/items/1")
                            .param("action", "PLUS")
                            .header("Referer", "/some-url"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/some-url"));
    }
}