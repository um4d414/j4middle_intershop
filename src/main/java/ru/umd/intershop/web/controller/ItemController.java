package ru.umd.intershop.web.controller;

import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.umd.intershop.common.constant.CartItemAction;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.service.item.ItemService;
import ru.umd.intershop.service.order.OrderService;
import ru.umd.intershop.web.model.ItemModel;
import ru.umd.intershop.web.model.PagingModel;

import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private static final int ITEM_ROW_SIZE = 3;

    private final ItemService itemService;

    private final OrderService orderService;

    @GetMapping("/main/items")
    public String mainPage(
        Model model,
        @RequestParam(defaultValue = "10") @Max(100) Integer pageSize,
        @RequestParam(defaultValue = "0") @Min(0) Integer pageNumber,
        @RequestParam(defaultValue = "NO") ItemSortingEnum sort,
        @RequestParam(required = false) @Size(min = 2) String search
    ) {
        var itemsPage = itemService.findAllActive(
            Pageable
                .ofSize(pageSize)
                .withPage(pageNumber),
            sort,
            search
        );

        var cart = orderService.getCart();

        var itemsDtoList = itemsPage.getContent()
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
                         .imgPath(item.getImagePath())
                         .title(item.getName())
                         .description(item.getDescription())
                         .price(item.getPrice())
                         .count(orderItem.isPresent() ? orderItem.get().getCount() : 0)
                         .build();
                 }
            )
            .toList();

        var structuredByRowItems = IntStream
            .range(0, (itemsDtoList.size() + ITEM_ROW_SIZE - 1) / ITEM_ROW_SIZE)
            .mapToObj(i -> itemsDtoList.subList(
                i * ITEM_ROW_SIZE,
                Math.min((i + 1) * ITEM_ROW_SIZE, itemsDtoList.size())
            ))
            .toList();


        model.addAttribute("paging", PagingModel.of(itemsPage));
        model.addAttribute("items", structuredByRowItems);

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
            .price(item.getPrice())
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
        orderService.updateItemCount(id, CartItemAction.valueOf(action.toUpperCase()));

        return "redirectBack";
    }
}
