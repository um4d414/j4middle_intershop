package ru.umd.intershop.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemModel {
    private String id;

    private String title;

    private String description;

    private String price;

    private String imgPath;
}
