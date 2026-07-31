-- ============================================================
-- V6__Phase5_Hardening.sql
-- Database Optimization and Async Jobs Table
-- ============================================================

-- 1. Create async_jobs table for tracking background AI operations
CREATE TABLE IF NOT EXISTS async_jobs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    result_payload JSONB,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_async_job_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. Add B-Tree Indexes on Foreign Keys (Crucial for performance and avoiding full table scans on joins)
CREATE INDEX IF NOT EXISTS idx_analyses_user_id ON analyses(user_id);
CREATE INDEX IF NOT EXISTS idx_analyses_resume_id ON analyses(resume_id);
CREATE INDEX IF NOT EXISTS idx_analyses_job_description_id ON analyses(job_description_id);

CREATE INDEX IF NOT EXISTS idx_improved_resumes_user_id ON improved_resumes(user_id);
CREATE INDEX IF NOT EXISTS idx_improved_resumes_analysis_id ON improved_resumes(analysis_id);

CREATE INDEX IF NOT EXISTS idx_job_descriptions_user_id ON job_descriptions(user_id);

CREATE INDEX IF NOT EXISTS idx_resumes_user_id ON resumes(user_id);

CREATE INDEX IF NOT EXISTS idx_roadmaps_user_id ON roadmaps(user_id);
CREATE INDEX IF NOT EXISTS idx_roadmaps_analysis_id ON roadmaps(analysis_id);

CREATE INDEX IF NOT EXISTS idx_async_jobs_user_id ON async_jobs(user_id);

-- 3. Add Index on Job Status for faster polling queries
CREATE INDEX IF NOT EXISTS idx_async_jobs_status ON async_jobs(status);
