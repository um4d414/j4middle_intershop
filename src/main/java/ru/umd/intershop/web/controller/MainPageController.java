package ru.umd.intershop.web.controller;

import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.service.product.ItemService;
import ru.umd.intershop.web.model.PagingModelAttribute;

@Controller
@RequiredArgsConstructor
public class MainPageController {
    private final ItemService itemService;

    @GetMapping
    public String mainPage(
        Model model,
        @RequestParam(defaultValue = "10") @Max(100) Integer pageSize,
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "NO") ItemSortingEnum sort
    ) {

        var items = itemService.findAll(
            Pageable
                .ofSize(pageSize)
                .withPage(pageNumber),
            sort
        );

        var pagingModelAttribute = PagingModelAttribute.of(items);
        model.addAttribute("paging", pagingModelAttribute);

        return "main";
    }
}
