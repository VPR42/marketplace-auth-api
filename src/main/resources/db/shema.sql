CREATE TABLE IF NOT EXISTS cities
(
    id     SERIAL PRIMARY KEY,
    region VARCHAR(55) NOT NULL,
    name   VARCHAR(55) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_region_city_name ON cities (region, name);


CREATE TABLE IF NOT EXISTS users
(
    id          UUID PRIMARY KEY,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    surname     VARCHAR(100) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    patronymic  VARCHAR(100) NOT NULL,
    avatar_path TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    city        INT          NOT NULL REFERENCES cities ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS skills
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(55) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS masters_info
(
    master_id     UUID         NOT NULL REFERENCES users ON DELETE CASCADE,
    experience    INT          NOT NULL CHECK (experience > 0),
    description   TEXT         NULL,
    pseudonym     VARCHAR(100) NULL UNIQUE,
    phone_number  VARCHAR(10)  NOT NULL UNIQUE,
    working_hours VARCHAR(25)  NULL -- костыль
);

CREATE TABLE IF NOT EXISTS master_skills
(
    master_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
    skill_id  INT  NOT NULL REFERENCES skills ON DELETE RESTRICT
);

-- Будем задавать вручную (Хранить энамами там все дела)
CREATE TABLE IF NOT EXISTS categories
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(55) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tags
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(55) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS jobs
(
    id          UUID PRIMARY KEY,
    master_id   UUID                     NOT NULL REFERENCES users ON DELETE CASCADE,
    name        VARCHAR(100)             NOT NULL,
    description TEXT                     NOT NULL,
    price       DECIMAL(5, 2)            NOT NULL CHECK (price > 0),
    cover_url   VARCHAR(255)             NULL,
    category_id INT                      NOT NULL REFERENCES categories ON DELETE RESTRICT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS job_tags
(
    job_id UUID NOT NULL REFERENCES jobs ON DELETE CASCADE,
    tag_id INT  NOT NULL REFERENCES tags ON DELETE RESTRICT,
    PRIMARY KEY (job_id, tag_id)
);

CREATE TABLE IF NOT EXISTS favourites
(
    user_id    UUID      NOT NULL REFERENCES users ON DELETE CASCADE,
    job_id     UUID      NOT NULL REFERENCES jobs ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, job_id)
);

CREATE TABLE IF NOT EXISTS orders
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    UUID                     NOT NULL REFERENCES users ON DELETE CASCADE,
    job_id     UUID                     NOT NULL REFERENCES jobs ON DELETE CASCADE,
    status     VARCHAR(15)              NOT NULL,
    ordered_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS messages
(
    id          UUID PRIMARY KEY,
    first_user  UUID          REFERENCES users (id) ON DELETE SET NULL,
    second_user UUID          REFERENCES users (id) ON DELETE SET NULL,
    message     VARCHAR(1000) NOT NULL,
    initiator   VARCHAR(1)    NOT NULL,
    sent_at     TIMESTAMP     NOT NULL
);
