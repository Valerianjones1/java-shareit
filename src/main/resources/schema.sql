DROP TABLE IF EXISTS users, items;
CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(320) UNIQUE,
    name  varchar(100)
);
CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        varchar(200),
    description varchar(200),
    user_id     BIGINT,
    is_available   boolean,
    CONSTRAINT fk_items_to_users FOREIGN KEY (user_id) references users (id)
);