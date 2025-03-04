package ru.umd.intershop.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.umd.intershop.service.admin.dto.ItemForm;
import ru.umd.intershop.service.admin.item.ItemAdminService;

@Controller
@RequiredArgsConstructor
public class ItemAdminController {
    private final ItemAdminService itemAdminService;

    @GetMapping("/items/add")
    public String showAddItemForm(Model model) {
        model.addAttribute("itemForm", new ItemForm());
        return "admin-item-add";
    }

    // Обработка отправки формы
    @PostMapping("/items/add")
    public String addItem(@ModelAttribute("itemForm") ItemForm itemForm) {
        itemAdminService.createItem(itemForm);
        // После успешного добавления можно перенаправить на список продуктов
        return "redirect:/items/add";
    }
}
