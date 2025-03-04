package ru.umd.intershop.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.data.repository.ItemRepository;
import ru.umd.intershop.service.dto.ItemDto;
import ru.umd.intershop.service.product.mapper.ItemServiceMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultItemService implements ItemService {
    private final ItemRepository itemRepository;

    private final ItemServiceMapper itemServiceMapper;

    @Override
    public Optional<ItemDto> findById(Long id) {
        return itemRepository
            .findById(id)
            .map(itemServiceMapper::map);
    }

    @Override
    public Page<ItemDto> findAllActive(Pageable pageable, ItemSortingEnum sort) {
        return itemRepository.findAllByIsActiveTrue(
                pageable,
                Sort.by(
                    Sort.Direction.ASC,
                    sort.getEntityField()
                )
            )
            .map(itemServiceMapper::map);
    }
}
