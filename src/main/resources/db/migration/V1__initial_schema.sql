-- ============================================================
-- V1__initial_schema.sql
-- CareerPilot AI — Initial Database Schema
-- ============================================================

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- ============================================================
-- RESUMES
-- ============================================================
CREATE TABLE resumes (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_name    VARCHAR(255) NOT NULL,
    raw_text     TEXT NOT NULL,
    uploaded_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_resumes_user_id ON resumes(user_id);

-- ============================================================
-- JOB DESCRIPTIONS
-- ============================================================
CREATE TABLE job_descriptions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(255),
    company     VARCHAR(255),
    raw_text    TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_job_descriptions_user_id ON job_descriptions(user_id);

-- ============================================================
-- ANALYSES
-- ============================================================
CREATE TABLE analyses (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    resume_id             UUID NOT NULL REFERENCES resumes(id) ON DELETE CASCADE,
    job_description_id    UUID NOT NULL REFERENCES job_descriptions(id) ON DELETE CASCADE,
    match_percentage      DECIMAL(5,2),
    matching_skills       JSONB,
    missing_skills        JSONB,
    strengths             JSONB,
    weaknesses            JSONB,
    raw_ai_response       TEXT,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_analyses_user_id ON analyses(user_id);
CREATE INDEX idx_analyses_resume_id ON analyses(resume_id);

-- ============================================================
-- IMPROVED RESUMES
-- ============================================================
CREATE TABLE improved_resumes (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id    UUID NOT NULL REFERENCES analyses(id) ON DELETE CASCADE,
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    summary        TEXT,
    experience     JSONB,
    projects       JSONB,
    skills         JSONB,
    full_text      TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_improved_resumes_user_id ON improved_resumes(user_id);
CREATE INDEX idx_improved_resumes_analysis_id ON improved_resumes(analysis_id);

-- ============================================================
-- ROADMAPS
-- ============================================================
CREATE TABLE roadmaps (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id    UUID NOT NULL REFERENCES analyses(id) ON DELETE CASCADE,
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skills         JSONB NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_roadmaps_user_id ON roadmaps(user_id);
CREATE INDEX idx_roadmaps_analysis_id ON roadmaps(analysis_id);
