package ru.umd.intershop.service.product;

import org.springframework.data.domain.*;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.service.dto.ItemDto;

import java.util.Optional;

public interface ItemService {
    Optional<ItemDto> findById(Long id);

    Page<ItemDto> findAll(Pageable pageable, ItemSortingEnum sort);
}
