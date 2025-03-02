package ru.umd.intershop.service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderDto {
    private Long id;

    private BigDecimal totalPrice;

    private List<Long> productIds;
}
