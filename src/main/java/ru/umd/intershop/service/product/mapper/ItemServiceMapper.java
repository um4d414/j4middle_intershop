package ru.umd.intershop.service.product.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.umd.intershop.data.entity.ItemEntity;
import ru.umd.intershop.service.dto.ItemDto;

@Component
public class ItemServiceMapper {
    @Value("${app.image-base-path}")
    private String imageBasePath;

    public ItemDto map(ItemEntity ItemEntity) {
        return ItemDto
            .builder()
            .id(ItemEntity.getId())
            .name(ItemEntity.getName())
            .price(ItemEntity.getPrice())
            .description(ItemEntity.getDescription())
            .imgPath(imageBasePath + ItemEntity.getImageFileName())
            .build();
    }
}
