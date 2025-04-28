CREATE TABLE IF NOT EXISTS task
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(20) NOT NULL,
    description VARCHAR(30) NOT NULL,
    userId      INT         NOT NULL
);
