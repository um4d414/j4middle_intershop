package ru.umd.intershop.service.admin.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ItemForm {
    private String name;

    private String description;

    private BigDecimal price;

    private MultipartFile imageFile;

    private Boolean isActive = true;
}
