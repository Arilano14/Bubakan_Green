package id.bubakangreen.app.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.QuizQuestion
import id.bubakangreen.app.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreQuizRepository(
    private val firestore: FirebaseFirestore
) : QuizRepository {

    private val quizCollection by lazy {
        firestore.collection("plant_quiz_questions")
    }

    override fun getQuestionsByPlant(plantId: String): Flow<Result<List<QuizQuestion>>> {
        return quizCollection
            .whereEqualTo("plantId", plantId)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toQuizQuestion() }
                Result.Success(list) as Result<List<QuizQuestion>>
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override suspend fun createQuestion(question: QuizQuestion): Result<String> {
        return try {
            val docRef = if (question.questionId.isNotBlank()) {
                quizCollection.document(question.questionId)
            } else {
                quizCollection.document()
            }
            val finalQuestion = question.copy(questionId = docRef.id)
            docRef.set(finalQuestion.toMap()).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    companion object {
        fun QuizQuestion.toMap(): Map<String, Any?> = mapOf(
            "questionId" to questionId,
            "plantId" to plantId,
            "question" to question,
            "optionA" to optionA,
            "optionB" to optionB,
            "optionC" to optionC,
            "correctAnswer" to correctAnswer,
            "explanation" to explanation,
            "order" to order,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )

        fun DocumentSnapshot.toQuizQuestion(): QuizQuestion? {
            if (!exists()) return null
            val qId = getString("questionId") ?: id
            val pId = getString("plantId") ?: return null
            val qText = getString("question") ?: return null
            val a = getString("optionA") ?: return null
            val b = getString("optionB") ?: return null
            val c = getString("optionC") ?: return null
            val correct = getString("correctAnswer")?.trim()?.uppercase() ?: return null
            if (correct !in listOf("A", "B", "C")) return null
            val explanation = getString("explanation") ?: ""
            val order = getLong("order")?.toInt() ?: 1
            val isActive = getBoolean("isActive") ?: true
            val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()

            return QuizQuestion(
                questionId = qId,
                plantId = pId,
                question = qText,
                optionA = a,
                optionB = b,
                optionC = c,
                correctAnswer = correct,
                explanation = explanation,
                order = order,
                isActive = isActive,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
