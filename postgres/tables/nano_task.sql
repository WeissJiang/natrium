CREATE TABLE IF NOT EXISTS nano_task
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR NOT NULL,
    description         VARCHAR     DEFAULT '',
    enabled             BOOLEAN     DEFAULT TRUE,
    options             JSONB       DEFAULT '{}',
    time_interval       INTERVAL    DEFAULT '30s',
    last_execution_time TIMESTAMPTZ DEFAULT NOW(),
    creation_time       TIMESTAMPTZ DEFAULT NOW()
);