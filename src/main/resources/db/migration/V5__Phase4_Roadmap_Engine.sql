-- ============================================================
-- V5__Phase4_Roadmap_Engine.sql
-- Update roadmaps table for structured phase-based roadmap engine
-- ============================================================

-- Drop old unstructured skills array
ALTER TABLE roadmaps
DROP COLUMN skills;

-- Add new JSONB column for the structured, phased roadmap
ALTER TABLE roadmaps
ADD COLUMN phases JSONB NOT NULL DEFAULT '[]'::jsonb;
