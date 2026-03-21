# Lessons Learned Register: VITAL Health - Android (v1.0)

## 1. Document Overview
This register documents key insights and experiences gained throughout the project lifecycle to inform future initiatives.

## 2. Successes (What Went Well)
- **Offline-First Architecture:** Using Room DB as a local caching layer proved successful for data residency and speed.
- **Supabase Integration:** Supabase provided a robust authentication and database solution that was significantly faster to implement than a custom backend.
- **Jetpack Compose Performance:** The UI felt modern and premium with minimal boilerplate compared to traditional XML layouts.
- **Early Design Reviews:** Weekly design demos with stakeholders helped catch layout issues before they were codified.

## 3. Improvements (Areas for Growth)
- **Supabase Sync Logic:** Initial sync logic was overly complex; future projects should start with a simpler "Last-Writer-Wins" strategy earlier.
- **Testing Coverage:** Automated UI testing for complex Jetpack Compose components was prioritized late, leading to last-minute bug fixes.
- **PDF Generation Speed:** The current Android `PdfDocument` API can be slow; a background worker (WorkManager) should have been included from the start for document exports.

## 4. Key Takeaways
- **1. Start with Architecture:** High-quality mobile apps need a solid DI and data repository foundation early.
- **2. Documentation is Critical:** Standardized documentation helped new developers onboard twice as fast.

## 5. Recommendation for Next Project
Prioritize "Offline-Primary" logic in the repository layer from Week 1 to avoid sync complexities later.
