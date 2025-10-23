CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    image_file_link VARCHAR(255),
    image_key VARCHAR(500) NOT NULL,
    title VARCHAR(250),
    description VARCHAR(2000)
);