# Plant Quiz & Learning Data Quality Control Report
**Document ID:** `docs/phase-1/QUIZ_QC.md`  
**Phase:** PHASE 1 — TESTING STABILIZATION & CRITICAL BUG REMEDIATION  
**Target Module:** `FirestoreQuizRepository.kt`, `DefaultLearningData.kt`, `QuizViewModel.kt`  
**Timestamp:** 2026-10-06  
**Status:** `VERIFIED & IMPLEMENTED`  

---

## 1. Problem Diagnosis: Missing Default Plant Quiz Content

### 1.1 Verified Incident
During physical testing, navigating to the Plant Detail quiz section displayed an empty state or failed to load questions for the 9 core default plants (Cabai Rawit, Tomat, Terong, Jahe Merah, Kunyit, Kencur, Temulawak, Lidah Buaya, Sereh).

### 1.2 Root Cause Analysis
- `DefaultLearningData.kt` contained 135 rich, high-quality botanical questions (15 per plant) authored for offline/initial state.
- In `FirestoreQuizRepository.kt`, questions were fetched exclusively from the remote Firestore collection `plant_quiz_questions`:
  ```kotlin
  val snapshot = firestore.collection(COLLECTION_QUIZ_QUESTIONS)
      .whereEqualTo("plantId", plantId)
      .whereEqualTo("isActive", true)
      .get().await()
  ```
- Because the live remote Firestore collection had not been populated with the 135 documents, `snapshot.documents` returned an empty list. The UI therefore displayed an empty quiz view.

---

## 2. Multi-Tier Fallback Implementation

We updated `FirestoreQuizRepository.kt` to enforce a graceful, two-tier architecture:

```kotlin
override suspend fun getQuestionsByPlant(plantId: String): List<QuizQuestion> {
    return try {
        val snapshot = firestore.collection(COLLECTION_QUIZ_QUESTIONS)
            .whereEqualTo("plantId", plantId)
            .whereEqualTo("isActive", true)
            .get()
            .await()

        val firestoreQuestions = snapshot.documents.mapNotNull { doc ->
            doc.toObject(QuizQuestionDto::class.java)?.toDomain(doc.id)
        }

        // Tier 1: Return remote Firestore questions if populated
        if (firestoreQuestions.isNotEmpty()) {
            firestoreQuestions
        } else {
            // Tier 2: Autonomous fallback to verified local question bank
            DefaultLearningData.quizQuestions.filter { 
                it.plantId == plantId && it.isActive 
            }
        }
    } catch (e: Exception) {
        // Tier 2 Fallback on network/offline failure
        DefaultLearningData.quizQuestions.filter { 
            it.plantId == plantId && it.isActive 
        }
    }
}
```

### Key Architectural Benefits:
1. **Immediate Out-of-the-Box Functionality:** Default plants display all 15 questions immediately without requiring manual Firestore console seeding.
2. **Zero Network Failure Penalty:** Offline visitors in physical gardens can still play the educational quiz using local caching.
3. **Dynamic Remote Override:** If an admin updates or adds questions to Firestore in the future, the app automatically prioritizes the remote questions over local defaults.

---

## 3. Custom Plant Defensive Handling

For newly created plants added via the Admin Dashboard:
- Admin-created plants have no pre-bundled questions in `DefaultLearningData`.
- When Firestore returns 0 questions for a custom plant, the repository safely returns `emptyList()`.
- The UI in `QuizScreen` and `PlantDetailScreen` gracefully presents:
  > *"Kuis belum tersedia untuk tanaman ini."*
- **No Crash / No Infinite Loading:** Defensive error states ensure the user can navigate back to the plant overview smoothly.

---

## 4. Collection Integrity & Anti-Duplication Compliance

In strict adherence to Phase 1L rules:
- **Canonical Collections Preserved:**
  - `/master_plants`
  - `/locations`
  - `/location_plants`
  - `/plant_quiz_questions`
- **Zero Duplicate Collections Created:**
  - No `/plants`, `/web_plants`, `/default_plants`, or `/quiz_data` collections were introduced.
- **Data Flow:**
  `Local Fallback (DefaultLearningData)` -> `Repository` -> `QuizViewModel` -> `Compose UI`.
