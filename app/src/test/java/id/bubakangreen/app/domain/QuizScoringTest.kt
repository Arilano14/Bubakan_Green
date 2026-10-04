package id.bubakangreen.app.domain

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.domain.model.QuizQuestion
import id.bubakangreen.app.domain.model.QuizResultState
import id.bubakangreen.app.domain.model.QuizSession
import org.junit.Test

class QuizScoringTest {

    private fun createDummyQuestions(): List<QuizQuestion> = (1..5).map { index ->
        QuizQuestion(
            questionId = "q_dummy_$index",
            plantId = "sereh",
            question = "Pertanyaan $index?",
            optionA = "Opsi A",
            optionB = "Opsi B",
            optionC = "Opsi C",
            correctAnswer = "A",
            explanation = "Penjelasan $index",
            order = index,
            isActive = true
        )
    }

    @Test
    fun sc01_zeroCorrect_yieldsScoreZero_andEncouragement() {
        val questions = createDummyQuestions()
        val session = QuizSession(
            plantId = "sereh",
            questions = questions,
            userAnswers = mapOf(0 to "B", 1 to "B", 2 to "C", 3 to "B", 4 to "C")
        )

        assertThat(session.correctCount).isEqualTo(0)
        assertThat(session.score).isEqualTo(0)
        assertThat(session.resultState).isEqualTo(QuizResultState.ENCOURAGEMENT)
    }

    @Test
    fun sc02_oneCorrect_yieldsScore20_andEncouragement() {
        val questions = createDummyQuestions()
        val session = QuizSession(
            plantId = "sereh",
            questions = questions,
            userAnswers = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "B", 4 to "C")
        )

        assertThat(session.correctCount).isEqualTo(1)
        assertThat(session.score).isEqualTo(20)
        assertThat(session.resultState).isEqualTo(QuizResultState.ENCOURAGEMENT)
    }

    @Test
    fun sc03_twoCorrect_yieldsScore40_andEncouragement() {
        val questions = createDummyQuestions()
        val session = QuizSession(
            plantId = "sereh",
            questions = questions,
            userAnswers = mapOf(0 to "A", 1 to "A", 2 to "C", 3 to "B", 4 to "C")
        )

        assertThat(session.correctCount).isEqualTo(2)
        assertThat(session.score).isEqualTo(40)
        assertThat(session.resultState).isEqualTo(QuizResultState.ENCOURAGEMENT)
    }

    @Test
    fun sc04_threeCorrect_yieldsScore60_andEncouragement_boundary() {
        val questions = createDummyQuestions()
        val session = QuizSession(
            plantId = "sereh",
            questions = questions,
            userAnswers = mapOf(0 to "A", 1 to "A", 2 to "A", 3 to "B", 4 to "C")
        )

        assertThat(session.correctCount).isEqualTo(3)
        assertThat(session.score).isEqualTo(60)
        // Score exactly 60 is strictly ENCOURAGEMENT boundary per specification
        assertThat(session.resultState).isEqualTo(QuizResultState.ENCOURAGEMENT)
    }

    @Test
    fun sc05_fourCorrect_yieldsScore80_andGood() {
        val questions = createDummyQuestions()
        val session = QuizSession(
            plantId = "sereh",
            questions = questions,
            userAnswers = mapOf(0 to "A", 1 to "A", 2 to "A", 3 to "A", 4 to "C")
        )

        assertThat(session.correctCount).isEqualTo(4)
        assertThat(session.score).isEqualTo(80)
        assertThat(session.resultState).isEqualTo(QuizResultState.GOOD)
    }

    @Test
    fun sc06_fiveCorrect_yieldsScore100_andPerfect() {
        val questions = createDummyQuestions()
        val session = QuizSession(
            plantId = "sereh",
            questions = questions,
            userAnswers = mapOf(0 to "A", 1 to "A", 2 to "A", 3 to "A", 4 to "A")
        )

        assertThat(session.correctCount).isEqualTo(5)
        assertThat(session.score).isEqualTo(100)
        assertThat(session.resultState).isEqualTo(QuizResultState.PERFECT)
    }

    @Test
    fun boundaryCheck_resultStateMapping() {
        val questions = createDummyQuestions()
        
        val session60 = QuizSession(plantId = "sereh", questions = questions, userAnswers = mapOf(0 to "A", 1 to "A", 2 to "A"))
        assertThat(session60.score).isEqualTo(60)
        assertThat(session60.resultState).isEqualTo(QuizResultState.ENCOURAGEMENT)

        val session80 = QuizSession(plantId = "sereh", questions = questions, userAnswers = mapOf(0 to "A", 1 to "A", 2 to "A", 3 to "A"))
        assertThat(session80.score).isEqualTo(80)
        assertThat(session80.resultState).isEqualTo(QuizResultState.GOOD)

        val session100 = QuizSession(plantId = "sereh", questions = questions, userAnswers = mapOf(0 to "A", 1 to "A", 2 to "A", 3 to "A", 4 to "A"))
        assertThat(session100.score).isEqualTo(100)
        assertThat(session100.resultState).isEqualTo(QuizResultState.PERFECT)
    }
}
