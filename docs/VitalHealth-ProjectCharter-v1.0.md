# Project Charter: VITAL Health - Android (v1.0)

## 1. Project Overview
**Project Name:** VITAL Health - Android  
**Project Manager:** [Name]  
**Sponsor:** [Name/Organization]  
**Date:** 2026-03-21  

## 2. Project Goal
The primary goal is to develop a premium, offline-first health tracking application for the Android platform. The application will empower users to monitor their vital signs, medications, and overall health progress through a secure, intuitive, and modern interface.

## 3. Project Scope
### In-Scope:
- **Health Tracking:** Logging of weight, blood pressure, heart rate, and mood.
- **Medication Management:** Tracking takes, refills, and schedules.
- **Smart Features:** Streak counter, trend alerts (elevated vitals), and refill countdowns.
- **Analytics:** Real-time trend charts (week/month/year) and PDF clinical reports.
- **Journaling:** Chronological health timeline with free-form notes.
- **Onboarding:** Multi-step user setup and goal setting.
- **Cloud Sync:** Secure backup and restore via Supabase.
- **Security:** Offline-first data storage (Room DB) with encrypted cloud sync.

### Out-of-Scope:
- Integration with external wearable devices (e.g., Apple Watch, Fitbit) in phase 1.
- Social media sharing features.
- Telemedicine consultations / direct doctor communication.

## 4. Key Stakeholders
- **Users:** Patients, athletes, and health-conscious individuals.
- **Development Team:** Mobile engineers, UI/UX designers, and backend developers.
- **Product Owner:** Defines the vision and prioritizes features.
- **Healthcare Professionals:** (Indirect) Recipients of generated health reports.

## 5. Success Criteria
- **User Adoption:** Reach [Target Number] of active users within 6 months.
- **Data Integrity:** 100% data consistency between offline and cloud storage.
- **Performance:** App launch time under 2 seconds; smooth animations at 60fps.
- **Engagement:** Average daily active use of at least 3 minutes.

## 6. Project Constraints & Assumptions
- **Constraint:** Target target Android SDK version [Current].
- **Constraint:** Budget is limited to [Budget Amount].
- **Assumption:** Users have access to an internet connection for cloud sync.
- **Assumption:** Supabase will provide high availability for backend services.

## 7. Approval
| Name | Role | Signature | Date |
| :--- | :--- | :--- | :--- |
| [Name] | Project Sponsor | | |
| [Name] | Project Manager | | |
