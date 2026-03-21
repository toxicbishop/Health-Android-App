# Meeting Minutes: VITAL Health - Android (2026-03-21)

## 1. Meeting Overview
**Date:** March 21, 2026  
**Time:** 10:00 AM - 11:00 AM  
**Location:** Zoom Call  
**Attendees:** PM, Android Lead, Backend Lead, UI Designer  

## 2. Agenda
1. Review progress on Core Health Tracking UI.
2. Discuss Cloud Sync conflict resolution strategy.
3. Finalize APK release signing approach.

## 3. Decisions Made
- **Decision D1:** Cloud sync will use a "Last-Writer-Wins" strategy for conflicting logs unless the logs are identical, in which case they will be merged.
- **Decision D2:** The "Streak Counter" will be reset only if a user misses more than 48 hours of logging to account for timezone differences.
- **Decision D3:** Use a custom adaptive launcher icon for the final release.

## 4. Action Items
| ID | Task | Owner | Due Date |
| :-- | :--- | :--- | :--- |
| **A1** | Implement conflict resolution logic in `HealthRepository`. | Backend Lead | 2026-03-23 |
| **A2** | Update `ic-launcher-foreground.xml` for better logo centering. | UI Designer | 2026-03-22 |
| **A3** | Establish a secure `.env` structure for Supabase credentials. | Android Lead | 2026-03-21 |

## 5. Next Meeting
- **Date:** March 24, 2026
- **Topic:** Smart Feature Sprint Kickoff.
