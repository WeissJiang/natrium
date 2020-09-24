-- nano_chat
DROP TABLE IF EXISTS nano_chat;
CREATE TABLE IF NOT EXISTS nano_chat
(
    id        BIGINT PRIMARY KEY,
    username  VARCHAR,
    title     VARCHAR,
    firstname VARCHAR,
    type      VARCHAR
);

-- nano_user
DROP TABLE IF EXISTS nano_user;
CREATE TABLE IF NOT EXISTS nano_user
(
    id            BIGINT PRIMARY KEY,
    username      VARCHAR,
    firstname     VARCHAR,
    is_bot        BOOL,
    language_code VARCHAR,
    email         VARCHAR
);

-- nano_token
DROP TABLE IF EXISTS nano_token;
CREATE TABLE IF NOT EXISTS nano_token
(
    id               SERIAL PRIMARY KEY,
    token            VARCHAR,
    name             VARCHAR,
    chat_id          BIGINT,
    user_id          BIGINT,
    status           VARCHAR DEFAULT 'VALID',
    privilege        JSONB,
    last_active_time TIMESTAMPTZ,
    creation_time    TIMESTAMPTZ,
    UNIQUE (token)
);
COMMENT ON COLUMN nano_token.status IS 'VALID,INVALID,VERIFICATING:{username}:{code}';
