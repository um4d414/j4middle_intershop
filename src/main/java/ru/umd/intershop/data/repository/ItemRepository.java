package ru.umd.intershop.data.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.umd.intershop.data.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    Page<ItemEntity> findAllByIsActiveTrue(Pageable pageable, Sort sort);
}
