package ru.umd.intershop.service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDto {
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String image;
}
