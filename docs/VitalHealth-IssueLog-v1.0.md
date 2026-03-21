# Issue Log: VITAL Health - Android (v1.0)

## 1. Issue Tracking Table
| ID | Issue Description | Severity | Date Identified | Assigned To | Status | Resolution |
| :-- | :--- | :--- | :--- | :--- | :--- | :--- |
| **I1** | **Supabase Email Rate Limit** | Medium | 2026-03-18 | Backend Lead | Resolved | Temporarily increased rate limits in Supabase dashboard for dev environment. |
| **I2** | **APK Signing Configuration** | High | 2026-03-19 | Android Lead | Open | Securely store signing keys (`vital-release-key.jks`) and exclude from Git. |
| **I3** | **Sync Conflicts (Offline vs. Cloud)** | Med-High | 2026-03-21 | Technical Lead | Open | Logs with the same timestamp from different devices are creating duplicates in Postgrest. |
| **I4** | **PDF Export File Size** | Low | 2026-03-20 | Dev Team | Open | Generated PDFs are over 5MB; research image compression for clinical reports. |

## 2. Resolution Detail (I1)
The dev team was unable to sign up more than 3 accounts per hour. By adjusting the "Email rate limit" in the Supabase Auth settings from 3/hr to 10/hr, testing could continue without interruption.

## 3. Escalation Process
Issues that remain "Open" for more than 5 business days with "High" severity must be escalated to the Project Sponsor immediately.
