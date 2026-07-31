-- ============================================================
-- V4__Phase3_Improvement_Engine.sql
-- Update improved_resumes table for structured bullet improvements
-- ============================================================

-- Drop old columns that are no longer needed
ALTER TABLE improved_resumes
DROP COLUMN experience,
DROP COLUMN projects,
DROP COLUMN skills,
DROP COLUMN full_text;

-- Add new JSONB columns for structured improvements and scores
ALTER TABLE improved_resumes
ADD COLUMN improvements JSONB,
ADD COLUMN scores JSONB;
