-- users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE
);

-- item_requests
CREATE TABLE IF NOT EXISTS item_requests (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(1000) NOT NULL,
    requester_id BIGINT NOT NULL REFERENCES users(id),
    created TIMESTAMP NOT NULL
);

-- items
CREATE TABLE IF NOT EXISTS items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    request_id BIGINT REFERENCES item_requests(id)
);

-- bookings
CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL REFERENCES items(id),
    booker_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING'
);

-- comments
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    item_id BIGINT NOT NULL REFERENCES items(id),
    author_id BIGINT NOT NULL REFERENCES users(id),
    created TIMESTAMP NOT NULL
);
