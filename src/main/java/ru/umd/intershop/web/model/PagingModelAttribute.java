package ru.umd.intershop.web.model;

import lombok.Builder;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Objects;

@Setter
@Builder
public class PagingModelAttribute {
    private Integer pageSize;

    private Integer pageNumber;

    private Boolean hasPrevious;

    private Boolean hasNext;

    public static PagingModelAttribute of(Page<?> page) {
        return PagingModelAttribute
            .builder()
            .pageSize(page.getSize())
            .pageNumber(page.getNumber())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();
    }

    public Integer pageSize() {
        return Objects.requireNonNullElse(this.pageSize, 0);
    }

    public Integer pageNumber() {
        return Objects.requireNonNullElse(this.pageNumber, 0);
    }

    public Boolean hasPrevious() {
        return Objects.requireNonNullElse(this.hasPrevious, false);
    }

    public Boolean hasNext() {
        return Objects.requireNonNullElse(this.hasNext, false);
    }
}
