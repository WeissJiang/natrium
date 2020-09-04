DROP TABLE IF EXISTS nano_session;
CREATE TABLE nano_session
(
    id                 SERIAL PRIMARY KEY,
    chat_id            BIGINT,
    user_id            BIGINT,
    attributes         JSON,
    creation_time      TIMESTAMPTZ,
    last_accessed_time TIMESTAMPTZ
);
