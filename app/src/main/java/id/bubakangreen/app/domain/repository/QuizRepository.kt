package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.QuizQuestion
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    /**
     * Retrieves all active quiz questions for a specific plant.
     * Expects 15 questions for default plants.
     */
    fun getQuestionsByPlant(plantId: String): Flow<Result<List<QuizQuestion>>>

    /**
     * Seeds or creates a quiz question (admin only).
     */
    suspend fun createQuestion(question: QuizQuestion): Result<String>
}
