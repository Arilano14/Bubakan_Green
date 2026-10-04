package id.bubakangreen.app.domain.model

/**
 * Botanical educational quiz question entity.
 * Stored in flat collection /plant_quiz_questions/{questionId}.
 * Strictly limited to 3 options (A, B, C) with general knowledge difficulty.
 */
data class QuizQuestion(
    val questionId: String,
    val plantId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val correctAnswer: String, // Strictly "A", "B", or "C"
    val explanation: String = "",
    val order: Int = 1,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    init {
        require(correctAnswer in listOf("A", "B", "C")) {
            "correctAnswer must strictly be 'A', 'B', or 'C', found: $correctAnswer"
        }
    }
}
