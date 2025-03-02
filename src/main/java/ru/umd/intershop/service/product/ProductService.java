package ru.umd.intershop.service.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.umd.intershop.service.dto.ProductDto;

import java.util.Optional;

public interface ProductService {
    Optional<ProductDto> findById(Long id);

    Page<ProductDto> findAll(Pageable pageable);
}
