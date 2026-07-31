-- ============================================================
-- V3__Phase2_Analysis_Engine.sql
-- Add new columns for Phase 2 Resume Analysis Engine
-- ============================================================

ALTER TABLE analyses
ADD COLUMN recommendations JSONB,
ADD COLUMN timing JSONB,
ADD COLUMN ai_model VARCHAR(100),
ADD COLUMN analysis_version VARCHAR(50);
