package ru.umd.intershop.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.umd.intershop.service.item.ItemService;
import ru.umd.intershop.service.order.OrderService;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final ItemService itemService;
}
