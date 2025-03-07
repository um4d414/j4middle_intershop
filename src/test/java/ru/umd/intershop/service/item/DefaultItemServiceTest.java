package ru.umd.intershop.service.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import ru.umd.intershop.common.constant.ItemSortingEnum;
import ru.umd.intershop.data.entity.ItemEntity;
import ru.umd.intershop.data.repository.ItemRepository;
import ru.umd.intershop.service.dto.ItemDto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class DefaultItemServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DefaultItemService defaultItemService;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    public void testFindByIdFound() {
        ItemEntity entity = ItemEntity.builder()
            .name("Test Item")
            .description("Test Description")
            .price(new BigDecimal("9.99"))
            .imageFileName("test.jpg")
            .isActive(true)
            .build();
        entity = itemRepository.save(entity);

        Optional<ItemDto> result = defaultItemService.findById(entity.getId());
        assertTrue(result.isPresent(), "Товар должен быть найден");
        ItemDto dto = result.get();
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getPrice(), dto.getPrice());
        assertEquals(entity.getImageFileName(), dto.getImageFileName());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<ItemDto> result = defaultItemService.findById(999L);
        assertFalse(result.isPresent(), "Товар с таким id отсутствует");
    }

    @Test
    public void testFindAllActiveWithoutSearch() {
        ItemEntity entity1 = ItemEntity.builder()
            .name("Alpha")
            .description("First item")
            .price(new BigDecimal("10.00"))
            .imageFileName("1.jpg")
            .isActive(true)
            .build();
        ItemEntity entity2 = ItemEntity.builder()
            .name("Beta")
            .description("Second item")
            .price(new BigDecimal("20.00"))
            .imageFileName("2.jpg")
            .isActive(true)
            .build();
        itemRepository.saveAll(Arrays.asList(entity1, entity2));

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemDto> result = defaultItemService.findAllActive(pageable, ItemSortingEnum.NO, null);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements(), "Должно быть 2 товара");
    }

    @Test
    public void testFindAllActiveWithSearch() {
        ItemEntity entity1 = ItemEntity.builder()
            .name("TestItem")
            .description("Test Description")
            .price(new BigDecimal("15.00"))
            .imageFileName("3.jpg")
            .isActive(true)
            .build();
        ItemEntity entity2 = ItemEntity.builder()
            .name("Other")
            .description("Other Description")
            .price(new BigDecimal("5.00"))
            .imageFileName("4.jpg")
            .isActive(true)
            .build();
        itemRepository.saveAll(Arrays.asList(entity1, entity2));

        Pageable pageable = PageRequest.of(0, 10);
        String search = "Test";
        Page<ItemDto> result = defaultItemService.findAllActive(pageable, ItemSortingEnum.NO, search);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements(), "Должен быть найден только один товар");
        ItemDto dto = result.getContent().get(0);
        assertEquals(entity1.getName(), dto.getName());
    }
}