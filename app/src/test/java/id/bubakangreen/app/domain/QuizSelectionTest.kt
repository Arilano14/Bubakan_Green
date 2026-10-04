package id.bubakangreen.app.domain

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.domain.model.QuizQuestion
import org.junit.Test

class QuizSelectionTest {

    private fun createBank(count: Int, plantId: String = "sereh"): List<QuizQuestion> {
        return (1..count).map { idx ->
            QuizQuestion(
                questionId = "q_${plantId}_$idx",
                plantId = plantId,
                question = "Soal nomor $idx?",
                optionA = "Jawaban A $idx",
                optionB = "Jawaban B $idx",
                optionC = "Jawaban C $idx",
                correctAnswer = if (idx % 3 == 0) "A" else if (idx % 3 == 1) "B" else "C",
                order = idx,
                isActive = true
            )
        }
    }

    @Test
    fun selection_from15Questions_takesExactly5UniqueQuestions() {
        val bank15 = createBank(15)
        val selected = bank15.shuffled().take(5)

        assertThat(selected).hasSize(5)
        val uniqueIds = selected.map { it.questionId }.toSet()
        assertThat(uniqueIds).hasSize(5)
    }

    @Test
    fun selection_from5Questions_takesAll5WithoutDuplicate() {
        val bank5 = createBank(5)
        val selected = bank5.shuffled().take(5)

        assertThat(selected).hasSize(5)
        val uniqueIds = selected.map { it.questionId }.toSet()
        assertThat(uniqueIds).hasSize(5)
    }

    @Test
    fun selection_below5Questions_isInsufficient() {
        val bank4 = createBank(4)
        val canStart = bank4.size >= 5
        assertThat(canStart).isFalse()

        val bank0 = createBank(0)
        val canStart0 = bank0.size >= 5
        assertThat(canStart0).isFalse()
    }

    @Test
    fun multipleSessions_produceValidSubsets() {
        val bank15 = createBank(15)
        val session1 = bank15.shuffled().take(5)
        val session2 = bank15.shuffled().take(5)

        assertThat(session1).hasSize(5)
        assertThat(session2).hasSize(5)
        assertThat(session1.map { it.questionId }.toSet()).hasSize(5)
        assertThat(session2.map { it.questionId }.toSet()).hasSize(5)
    }
}
