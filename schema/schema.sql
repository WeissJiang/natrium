-- nano_session
DROP TABLE IF EXISTS nano_session;
CREATE TABLE IF NOT EXISTS nano_session
(
    id                 SERIAL PRIMARY KEY,
    chat_id            BIGINT,
    user_id            BIGINT,
    attributes         JSON,
    creation_time      TIMESTAMPTZ,
    last_accessed_time TIMESTAMPTZ,
    UNIQUE (chat_id, user_id)
);
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
-- nano_chat
DROP TABLE IF EXISTS nano_user;
CREATE TABLE IF NOT EXISTS nano_user
(
    id            BIGINT PRIMARY KEY,
    username      VARCHAR,
    firstname     VARCHAR,
    is_bot        BOOL,
    language_code VARCHAR
);
