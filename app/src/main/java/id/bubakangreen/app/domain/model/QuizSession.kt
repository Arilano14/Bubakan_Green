package id.bubakangreen.app.domain.model

enum class QuizResultState {
    PERFECT,       // score == 100
    GOOD,          // score in 61..99
    ENCOURAGEMENT  // score <= 60
}

/**
 * In-memory local session state for a 5-question quiz attempt.
 * Never persisted to Firestore for MVP compliance.
 */
data class QuizSession(
    val plantId: String,
    val questions: List<QuizQuestion>,
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswerConfirmed: Boolean = false,
    val userAnswers: Map<Int, String> = emptyMap(), // questionIndex -> "A", "B", or "C"
    val isFinished: Boolean = false
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)

    val totalQuestions: Int
        get() = questions.size

    val correctCount: Int
        get() = questions.indices.count { index ->
            val answered = userAnswers[index]
            answered != null && answered.equals(questions[index].correctAnswer, ignoreCase = true)
        }

    val score: Int
        get() = if (totalQuestions > 0) {
            ((correctCount.toDouble() / totalQuestions.toDouble()) * 100).toInt()
        } else {
            0
        }

    val resultState: QuizResultState
        get() = when {
            score == 100 -> QuizResultState.PERFECT
            score in 61..99 -> QuizResultState.GOOD
            else -> QuizResultState.ENCOURAGEMENT
        }
}
