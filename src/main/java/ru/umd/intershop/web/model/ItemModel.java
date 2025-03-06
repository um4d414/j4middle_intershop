package ru.umd.intershop.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemModel {
    private Long id;

    private String imgPath;

    private String title;

    private String price;

    private String description;

    private Integer count;
}
