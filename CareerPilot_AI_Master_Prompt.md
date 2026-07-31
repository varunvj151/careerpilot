# CareerPilot AI --- Master Build Specification

## Objective

Build a **production-quality AI Resume Intelligence Platform** suitable
for a software engineering portfolio and resume.

The application must prioritize **quality over quantity**. Do not add
extra modules beyond the defined scope.

------------------------------------------------------------------------

# Core Philosophy

-   Implement only three major features.
-   Every feature should be polished, complete, and production-ready.
-   Favor maintainability, clean architecture, testing, and user
    experience over feature count.
-   Do not generate placeholder implementations.

------------------------------------------------------------------------

# Product Name

**CareerPilot AI**

**Tagline:** AI-powered Resume Intelligence Platform

------------------------------------------------------------------------

# Tech Stack

## Frontend

-   React 19
-   TypeScript
-   Vite
-   Tailwind CSS
-   shadcn/ui
-   React Router
-   React Hook Form
-   Axios
-   TanStack Query

## Backend

-   Java 21
-   Spring Boot 3
-   Spring Security
-   JWT Authentication
-   Spring Data JPA
-   PostgreSQL
-   Maven
-   Flyway
-   MapStruct
-   Lombok

## AI

-   Google Gemini API
-   LangChain4j
-   Apache PDFBox
-   Embedding-based semantic similarity (avoid simple keyword matching)

## DevOps

-   Docker
-   Docker Compose
-   GitHub Actions

------------------------------------------------------------------------

# Core Features

## Feature 1 --- Resume vs Job Description Analysis

### Flow

1.  Register/Login
2.  Upload PDF Resume
3.  Paste Job Description
4.  Click Analyze

### Backend Responsibilities

-   Extract PDF text
-   Clean extracted text
-   Parse sections:
    -   Summary
    -   Skills
    -   Education
    -   Projects
    -   Experience
-   Parse Job Description
-   Compare semantically
-   Produce structured analysis

### Output

-   Overall Match Score
-   Skills Match
-   Missing Skills
-   Resume Strengths
-   Resume Weaknesses
-   ATS Formatting Feedback
-   Improvement Suggestions

The score must be explainable through category scores.

------------------------------------------------------------------------

## Feature 2 --- AI Resume Improvement

The AI may rewrite wording but **must never invent experience**.

Improve:

-   Summary
-   Project descriptions
-   Experience bullets
-   Skills wording

Show:

Left side: Original

Right side: Improved

Allow user to copy the improved version.

------------------------------------------------------------------------

## Feature 3 --- Skill Gap Roadmap

Based on analysis generate:

For each missing skill:

-   Priority
-   Why it matters
-   Estimated learning time
-   Mini project suggestion

Example:

Docker

Reason: Common requirement for backend roles.

Mini Project: Containerize your Spring Boot application.

Estimated Time: 5 days

------------------------------------------------------------------------

# Explicitly Out of Scope

Do NOT build:

-   Recruiter dashboard
-   Cover letter generator
-   Mock interview
-   Chatbot
-   Job tracker
-   Payments
-   Notifications
-   Social login
-   Admin panel

------------------------------------------------------------------------

# Pages

-   Landing
-   Login
-   Register
-   Dashboard
-   Resume Analysis
-   Resume Improvement
-   Skill Roadmap
-   Profile
-   Settings

------------------------------------------------------------------------

# Backend Architecture

Use Clean Architecture.

Packages:

-   config
-   security
-   controller
-   service
-   repository
-   dto
-   entity
-   mapper
-   validation
-   exception
-   ai
-   util

Requirements:

-   DTOs only across API
-   Global exception handler
-   Validation
-   Centralized logging
-   REST best practices

------------------------------------------------------------------------

# Database

Tables

Users

-   id
-   name
-   email
-   password
-   created_at

Resumes

-   id
-   user_id
-   file_name
-   extracted_text
-   uploaded_at

JobDescriptions

-   id
-   user_id
-   content

Analyses

-   id
-   resume_id
-   job_description_id
-   overall_score
-   skills_score
-   ats_score
-   strengths
-   weaknesses
-   missing_skills
-   created_at

ImprovedResumes

-   id
-   analysis_id
-   improved_summary
-   improved_projects
-   improved_experience

Roadmaps

-   id
-   analysis_id
-   skill_name
-   priority
-   reason
-   learning_time
-   mini_project

------------------------------------------------------------------------

# UI Guidelines

Modern, minimal and professional.

Inspired by: - Linear - Stripe - Notion

Primary color: Blue

Spacing: Generous

Responsive: Desktop, tablet, mobile

Include: - Skeleton loaders - Error states - Empty states - Form
validation - Accessible components

------------------------------------------------------------------------

# API Design

Authentication

POST /api/auth/register

POST /api/auth/login

Resume

POST /api/resumes/upload

GET /api/resumes

Analysis

POST /api/analysis

GET /api/analysis/{id}

Resume Improvement

POST /api/improvement/{analysisId}

Roadmap

GET /api/roadmap/{analysisId}

------------------------------------------------------------------------

# Security

-   JWT Authentication
-   BCrypt passwords
-   Role-based authorization
-   Input validation
-   File size validation
-   PDF-only uploads
-   CORS configuration

------------------------------------------------------------------------

# Testing

Backend

-   Unit Tests
-   Integration Tests

Frontend

-   Component Tests

------------------------------------------------------------------------

# Deployment

Provide:

-   Dockerfile (frontend)
-   Dockerfile (backend)
-   docker-compose.yml
-   GitHub Actions workflow
-   Environment variable documentation

------------------------------------------------------------------------

# Documentation

Generate:

-   README.md
-   API documentation
-   Installation guide
-   Architecture diagram (Mermaid)
-   Database ER diagram (Mermaid)

------------------------------------------------------------------------

# Implementation Rules

Do NOT generate the entire project in one response.

Follow this order:

1.  Architecture
2.  Folder Structure
3.  Database
4.  Authentication
5.  Resume Upload
6.  Analysis Engine
7.  Resume Improvement
8.  Skill Roadmap
9.  Frontend Integration
10. Testing
11. Docker
12. CI/CD
13. Documentation

After each milestone, pause for approval before continuing.

The final codebase should be production-ready, clean, modular, and
suitable to showcase advanced full-stack engineering skills on a resume.
