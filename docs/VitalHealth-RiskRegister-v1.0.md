# Risk Register: VITAL Health - Android (v1.0)

## 1. Risk Identification & Mitigation Matrix

| ID | Risk Description | Impact (1-5) | Probability (1-5) | Severity | Mitigation Strategy | Owner |
| :-- | :--- | :--- | :--- | :--- | :--- | :--- |
| **R1** | **Supabase API Downtime** | 4 | 2 | 8 | Implement robust offline-first caching (Room DB); queue sync tasks to retry. | Backend Lead |
| **R2** | **Project Budget Overrun** | 3 | 3 | 9 | Use agile methodologies for priority-based feature delivery; weekly budget tracking. | Project Manager |
| **R3** | **Security Breach (Data Leak)** | 5 | 1 | 5 | Encrypt sensitive health data locally; use Supabase Row-Level Security (RLS) for cloud storage. | Technical Lead |
| **R4** | **Scope Creep** | 2 | 4 | 8 | Require formal "Change Request" approval for any feature additions outside the Charter. | Project Manager |
| **R5** | **Android OS Compatibility Issues** | 4 | 2 | 8 | Regular testing on target SDK versions; use Jetpack Compose for consistent UI. | Android Lead |
| **R6** | **Low User Engagement** | 3 | 2 | 6 | Enhance onboarding flow and implement gamified "Streak Counting" to incentivize daily use. | UI/UX Designer |

## 2. Risk Tracking Status
*Last updated: 2026-03-21*

- **Current Status:** All risks are currently managed. 
- **Active Issues:** None at this stage of the project.

## 3. Contingency Plan
In the event of a high-severity risk occurrence (e.g., R3), the project will pause all feature development for a maximum of 48 hours to dedicate the entire team to remediation and security auditing.
