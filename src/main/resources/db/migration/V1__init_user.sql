CREATE TYPE user_role AS ENUM ('ADMIN', 'ANNOTATOR', 'REVIEWER');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'BANNED');

CREATE TABLE "user" (
                       user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password BYTEA NOT NULL,
                       role user_role NOT NULL DEFAULT 'ANNOTATOR',
                       status user_status NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_email ON "user" (email);
CREATE INDEX idx_users_status ON "user" (status);
