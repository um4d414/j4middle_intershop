package ru.umd.intershop.web.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.umd.intershop.common.constant.ItemCountAction;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.service.item.ItemService;
import ru.umd.intershop.service.order.OrderService;
import ru.umd.intershop.web.model.ItemModel;
import ru.umd.intershop.web.model.PagingModel;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private final OrderService orderService;

    @GetMapping("/main/items")
    public String mainPage(
        Model model,
        @RequestParam(defaultValue = "10") @Max(100) Integer pageSize,
        @RequestParam(defaultValue = "0") @Min(0) Integer pageNumber,
        @RequestParam(defaultValue = "NO") ItemSortingEnum sort
    ) {
        var items = itemService.findAllActive(
            Pageable
                .ofSize(pageSize)
                .withPage(pageNumber),
            sort
        );

        var cart = orderService.getCart();

        model.addAttribute("paging", PagingModel.of(items));
        model.addAttribute(
            "items",
            items
                .getContent()
                .stream()
                .map(item -> {
                         var orderItem = cart
                             .getItems()
                             .stream()
                             .filter(it -> it.getItem().getId().equals(item.getId()))
                             .findFirst();

                         return ItemModel
                             .builder()
                             .id(item.getId())
                             .imgPath("images/" + item.getImageFileName())
                             .title(item.getName())
                             .description(item.getDescription())
                             .price(item.getPrice().toPlainString())
                             .count(orderItem.isPresent() ? orderItem.get().getCount() : 0)
                             .build();
                     }
                )
        );

        return "main";
    }

    @GetMapping(path = "/items/{id}")
    public String itemPage(
        Model model,
        @PathVariable Long id
    ) {
        var item = itemService.findById(id).orElseThrow();
        var cart = orderService.getCart();

        var orderItem = cart
            .getItems()
            .stream()
            .filter(it -> it.getItem().getId().equals(item.getId()))
            .findFirst();

        var itemModel = ItemModel
            .builder()
            .id(item.getId())
            .imgPath("images/" + item.getImageFileName())
            .title(item.getName())
            .description(item.getDescription())
            .price(item.getPrice().toPlainString())
            .count(orderItem.isPresent() ? orderItem.get().getCount() : 0)
            .build();

        model.addAttribute(
            "item",
            itemModel
        );

        return "item";
    }

    @PostMapping("/items/{id}")
    public String updateCartItem(
        @PathVariable Long id,
        @RequestParam("action") String action
    ) {
        orderService.updateItemCount(id, ItemCountAction.valueOf(action.toUpperCase()));

        return "redirectBack";
    }
}
