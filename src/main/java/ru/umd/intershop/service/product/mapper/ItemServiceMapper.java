package ru.umd.intershop.service.product.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.umd.intershop.data.entity.ItemEntity;
import ru.umd.intershop.service.dto.ItemDto;

@Component
public class ItemServiceMapper {
    @Value("${app.image-base-path}")
    private String imageBasePath;

    public ItemDto map(ItemEntity itemEntity) {
        return ItemDto
            .builder()
            .id(itemEntity.getId())
            .name(itemEntity.getName())
            .price(itemEntity.getPrice())
            .description(itemEntity.getDescription())
            .imgPath(imageBasePath + itemEntity.getImageFileName())
            .build();
    }
}