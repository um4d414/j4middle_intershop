-- liquibase formatted sql

-- changeset varos:1741357349963-1
CREATE TABLE items
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    name            VARCHAR(255)                            NOT NULL,
    description     VARCHAR(255),
    price           DECIMAL,
    image_file_name VARCHAR(255),
    is_active       BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

-- changeset varos:1741357349963-2
CREATE TABLE order_items
(
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    count      INTEGER NOT NULL,
    order_id   BIGINT  NOT NULL,
    item_id    BIGINT  NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (order_id, item_id)
);

-- changeset varos:1741357349963-3
CREATE TABLE orders
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    status      VARCHAR(255)                            NOT NULL,
    total_price DECIMAL                                 NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

-- changeset varos:1741357349963-4
ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (id);

-- changeset varos:1741357349963-5
ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

