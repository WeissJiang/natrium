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
    status           VARCHAR     DEFAULT 'VALID',
    privilege        JSONB       DEFAULT '[]',
    last_active_time TIMESTAMPTZ,
    creation_time    TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (token)
);
COMMENT ON COLUMN nano_token.status IS 'VALID,INVALID,VERIFYING:{username}:{code}';

-- nano_task
DROP TABLE IF EXISTS nano_task;
CREATE TABLE IF NOT EXISTS nano_task
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR NOT NULL,
    description         VARCHAR     DEFAULT '',
    enabled             BOOL        DEFAULT TRUE,
    options             JSONB       DEFAULT '{}',
    time_interval       INTERVAL    DEFAULT '30s',
    last_execution_time TIMESTAMPTZ DEFAULT NOW(),
    creation_time       TIMESTAMPTZ DEFAULT NOW()
);

-- key_value
DROP TABLE IF EXISTS key_value;
CREATE TABLE IF NOT EXISTS key_value
(
    id                SERIAL PRIMARY KEY,
    key               VARCHAR NOT NULL,
    value             JSONB       DEFAULT '{}',
    last_updated_time TIMESTAMPTZ,
    creation_time     TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (key)
);

-- nano_blob
DROP TABLE IF EXISTS nano_blob;
CREATE TABLE IF NOT EXISTS nano_blob
(
    id   SERIAL PRIMARY KEY,
    key  VARCHAR NOT NULL,
    blob TEXT DEFAULT '',
    UNIQUE (key)
);