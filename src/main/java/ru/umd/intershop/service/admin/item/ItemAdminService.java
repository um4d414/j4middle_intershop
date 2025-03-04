package ru.umd.intershop.service.admin.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.umd.intershop.data.entity.ItemEntity;
import ru.umd.intershop.data.repository.ItemRepository;
import ru.umd.intershop.service.admin.dto.ItemForm;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemAdminService {
    @Value("${app.image-base-path}")
    private String imageBasePath;

    private final ItemRepository itemRepository;

    public void createItem(ItemForm itemForm) {
        var file = itemForm.getImageFile();
        String newFileName = null;

        if (file != null) {
            var originalFilename = file.getOriginalFilename();
            var extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            newFileName = UUID.randomUUID() + extension;

            try {
                File uploadDir = new File(imageBasePath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                File dest = new File(uploadDir, newFileName);
                file.transferTo(dest);

            } catch (IOException e) {
                throw new RuntimeException("Ошибка сохранения файла", e);
            }
        }

        ItemEntity itemEntity = ItemEntity.builder()
            .name(itemForm.getName())
            .description(itemForm.getDescription())
            .price(itemForm.getPrice())
            .imageFileName(newFileName)
            .isActive(itemForm.getIsActive() != null ? itemForm.getIsActive() : true)
            .build();

        itemRepository.save(itemEntity);
    }
}
