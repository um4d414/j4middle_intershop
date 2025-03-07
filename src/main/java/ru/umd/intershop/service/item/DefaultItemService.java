package ru.umd.intershop.service.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.data.entity.ItemEntity;
import ru.umd.intershop.data.repository.ItemRepository;
import ru.umd.intershop.service.dto.ItemDto;
import ru.umd.intershop.service.item.mapper.ItemServiceMapper;

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
    public Page<ItemDto> findAllActive(
        Pageable pageable,
        ItemSortingEnum sort,
        String search
    ) {
        Page<ItemEntity> itemEntityList;

        if (search == null || search.isEmpty()) {
            itemEntityList = itemRepository.findAllByIsActiveTrue(
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(sort.getEntityField()).ascending()
                )
            );
        } else {
            itemEntityList = itemRepository.findAllByIsActiveTrueAndNameLikeIgnoreCase(
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(sort.getEntityField()).ascending()
                ),
                "%" + search + "%"
            );
        }

        return itemEntityList.map(itemServiceMapper::map);
    }
}
