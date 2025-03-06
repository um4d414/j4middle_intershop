package ru.umd.intershop.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.umd.intershop.service.order.OrderService;
import ru.umd.intershop.web.model.ItemModel;
import ru.umd.intershop.web.model.OrderModel;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public String orders(Model model) {
        var orderModels = orderService
            .findAllCompleted()
            .stream()
            .map(
                orderDto -> OrderModel
                    .builder()
                    .id(orderDto.getId())
                    .items(
                        orderDto.getItems()
                            .stream()
                            .map(orderItemDto -> ItemModel
                                .builder()
                                .title(orderItemDto.getItem().getName())
                                .count(orderItemDto.getCount())
                                .price(orderItemDto.getItem().getPrice())
                                .build()
                            )
                            .toList()
                    )
                    .total(orderDto.getTotalPrice().toPlainString())
                    .build()
            )
            .toList();

        model.addAttribute("orders", orderModels);

        return "orders";
    }

    @GetMapping(value = "/orders/{id}")
    public String order(
        Model model,
        @PathVariable("id") Long id,
        @RequestParam(required = false, defaultValue = "false") Boolean isNew
    ) {
        var orderModel = orderService
            .findById(id)
            .map(
                orderDto -> OrderModel
                    .builder()
                    .id(orderDto.getId())
                    .items(
                        orderDto.getItems()
                            .stream()
                            .map(orderItemDto -> ItemModel
                                .builder()
                                .id(orderItemDto.getItem().getId())
                                .title(orderItemDto.getItem().getName())
                                .description(orderItemDto.getItem().getDescription())
                                .imgPath(orderItemDto.getItem().getImagePath())
                                .count(orderItemDto.getCount())
                                .price(orderItemDto.getItem().getPrice())
                                .build()
                            )
                            .toList()
                    )
                    .total(orderDto.getTotalPrice().toPlainString())
                    .build()
            )
            .orElseThrow();

        model.addAttribute("order", orderModel);
        model.addAttribute("newOrder", isNew);

        return "order";
    }

    @GetMapping(value = "/cart/items")
    public String cart(Model model) {
        var cart = orderService.getCart();

        var items = cart.getItems()
            .stream()
            .map(orderItemDto -> ItemModel
                .builder()
                .id(orderItemDto.getItem().getId())
                .title(orderItemDto.getItem().getName())
                .description(orderItemDto.getItem().getDescription())
                .imgPath(orderItemDto.getItem().getImagePath())
                .count(orderItemDto.getCount())
                .price(orderItemDto.getItem().getPrice())
                .build()
            )
            .toList();

        model.addAttribute("items", items);
        model.addAttribute("empty", items.isEmpty());
        model.addAttribute(
            "total",
            cart.getItems().stream()
                .map(
                    orderItem -> orderItem.getItem().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getCount()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        return "cart";
    }

    @PostMapping(value = "/buy")
    public String processOrder() {
        var orderId = orderService.processCart();

        return "redirect:/orders/" + orderId + "?isNew=true";
    }
}
