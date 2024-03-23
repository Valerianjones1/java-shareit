CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(320),
    name  varchar(100)
);
CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        varchar(200),
    description varchar(200),
    user_id     BIGINT,
    available   boolean,
    CONSTRAINT fk_items_to_users FOREIGN KEY (user_id) references users (id)
);