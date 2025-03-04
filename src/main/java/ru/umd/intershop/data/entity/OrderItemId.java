package ru.umd.intershop.data.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@Getter
public class OrderItemId implements Serializable {
    private Long orderId;

    private Long itemId;
}
