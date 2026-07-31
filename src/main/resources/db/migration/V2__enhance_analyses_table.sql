-- ============================================================
-- V2__enhance_analyses_table.sql
-- Add JSONB columns to store detailed parsed data and scores
-- ============================================================

ALTER TABLE analyses
ADD COLUMN extracted_resume JSONB,
ADD COLUMN extracted_jd JSONB,
ADD COLUMN scores JSONB;
