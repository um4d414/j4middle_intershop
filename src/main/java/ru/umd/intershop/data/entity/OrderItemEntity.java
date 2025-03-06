package ru.umd.intershop.data.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.umd.intershop.data.entity.embedded.OrderItemId;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItemEntity extends BaseEntity {
    @EmbeddedId
    @Builder.Default
    private OrderItemId id = new OrderItemId();

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Column(nullable = false)
    private int count;
}