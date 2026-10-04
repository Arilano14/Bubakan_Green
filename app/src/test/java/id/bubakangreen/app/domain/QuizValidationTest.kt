package id.bubakangreen.app.domain

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.data.fixture.DefaultLearningData
import org.junit.Test

class QuizValidationTest {

    private val expectedPlants = listOf(
        "sereh",
        "cabai",
        "kangkung",
        "tomat",
        "terong",
        "jahe",
        "kencur",
        "kunyit",
        "lidah_buaya"
    )

    @Test
    fun defaultQuizQuestions_hasExactly135Questions() {
        val questions = DefaultLearningData.quizQuestions
        assertThat(questions).hasSize(135)
    }

    @Test
    fun defaultQuizQuestions_coversAll9Plants_withExactly15QuestionsEach() {
        val questions = DefaultLearningData.quizQuestions
        val grouped = questions.groupBy { it.plantId }

        assertThat(grouped.keys).containsExactlyElementsIn(expectedPlants)

        for (plantId in expectedPlants) {
            val plantQuestions = grouped[plantId]
            assertThat(plantQuestions).isNotNull()
            assertThat(plantQuestions).hasSize(15)
        }
    }

    @Test
    fun allQuestions_haveValidOptionsAndCorrectAnswers() {
        val questions = DefaultLearningData.quizQuestions

        for (q in questions) {
            assertThat(q.question.isNotBlank()).isTrue()
            assertThat(q.optionA.isNotBlank()).isTrue()
            assertThat(q.optionB.isNotBlank()).isTrue()
            assertThat(q.optionC.isNotBlank()).isTrue()

            // Distinct options invariant
            assertThat(q.optionA).isNotEqualTo(q.optionB)
            assertThat(q.optionA).isNotEqualTo(q.optionC)
            assertThat(q.optionB).isNotEqualTo(q.optionC)

            // Strictly A, B, or C
            assertThat(q.correctAnswer).isIn(listOf("A", "B", "C"))
            assertThat(q.isActive).isTrue()
            assertThat(q.order).isIn(1..15)
        }
    }

    @Test
    fun noDuplicateQuestions_withinSamePlant() {
        val questions = DefaultLearningData.quizQuestions
        val grouped = questions.groupBy { it.plantId }

        for ((plantId, list) in grouped) {
            val texts = list.map { it.question.trim().lowercase() }
            val uniqueTexts = texts.toSet()
            assertThat(uniqueTexts.size).isEqualTo(list.size)
        }
    }

    @Test
    fun plantVoices_hasExactly9DefaultPlants() {
        val voices = DefaultLearningData.plantVoices
        assertThat(voices).hasSize(9)

        val voicePlantIds = voices.map { it.plantId }
        assertThat(voicePlantIds).containsExactlyElementsIn(expectedPlants)

        for (v in voices) {
            assertThat(v.language).isEqualTo("zh-CN")
            assertThat(v.isActive).isTrue()
        }
    }
}
