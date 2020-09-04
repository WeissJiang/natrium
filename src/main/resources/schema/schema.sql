-- nano_session
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
-- nano_session unique index
CREATE UNIQUE INDEX unique_index_chat_id_user_id ON nano_session (chat_id, user_id)
