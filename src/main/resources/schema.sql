DROP TABLE IF EXISTS users,items,bookings,requests,comments;
CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(320) UNIQUE,
    name  varchar(100)
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    requestor_id BIGINT,
    description  varchar(200),
    created      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requestor_id) references users (id)
);
CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         varchar(200),
    description  varchar(200),
    user_id      BIGINT,
    request_id   BIGINT,
    is_available boolean,
    CONSTRAINT fk_items_to_users FOREIGN KEY (user_id) references users (id),
    CONSTRAINT fk_items_to_item_requests FOREIGN KEY (request_id) references requests (id)
);
CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status     varchar(200),
    booker_id  BIGINT,
    item_id    BIGINT,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) references users (id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) references items (id)
);
CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id BIGINT,
    item_id   BIGINT,
    text      varchar(200),
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) references users (id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) references items (id)
);