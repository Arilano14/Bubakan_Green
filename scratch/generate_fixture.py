import json
import os

with open('docs/data/DEFAULT_QUIZ_QUESTIONS.json', 'r', encoding='utf-8') as f:
    questions = json.load(f)

with open('docs/data/DEFAULT_PLANT_VOICES.json', 'r', encoding='utf-8') as f:
    voices = json.load(f)

code = '''package id.bubakangreen.app.data.fixture

import id.bubakangreen.app.domain.model.PlantVoice
import id.bubakangreen.app.domain.model.QuizQuestion

/**
 * Authoritative default learning content fixtures.
 * Synchronized exactly with docs/data/DEFAULT_QUIZ_QUESTIONS.json
 * and docs/data/DEFAULT_PLANT_VOICES.json.
 */
object DefaultLearningData {

    val plantVoices: List<PlantVoice> = listOf(
'''

for v in voices:
    code += f'''        PlantVoice(
            plantId = "{v['plantId']}",
            language = "{v['language']}",
            audioUrl = {f'"{v["audioUrl"]}"' if v['audioUrl'] else 'null'},
            isActive = {str(v['isActive']).lower()},
            createdAt = {v['createdAt']}L,
            updatedAt = {v['updatedAt']}L
        ),
'''

code += '''    )

    val quizQuestions: List<QuizQuestion> = listOf(
'''

for q in questions:
    q_esc = q['question'].replace('"', '\\"')
    a_esc = q['optionA'].replace('"', '\\"')
    b_esc = q['optionB'].replace('"', '\\"')
    c_esc = q['optionC'].replace('"', '\\"')
    exp_esc = q['explanation'].replace('"', '\\"')
    code += f'''        QuizQuestion(
            questionId = "{q['questionId']}",
            plantId = "{q['plantId']}",
            question = "{q_esc}",
            optionA = "{a_esc}",
            optionB = "{b_esc}",
            optionC = "{c_esc}",
            correctAnswer = "{q['correctAnswer']}",
            explanation = "{exp_esc}",
            order = {q['order']},
            isActive = {str(q['isActive']).lower()},
            createdAt = {q['createdAt']}L,
            updatedAt = {q['updatedAt']}L
        ),
'''

code += '''    )
}
'''

os.makedirs('app/src/main/java/id/bubakangreen/app/data/fixture', exist_ok=True)
out_path = 'app/src/main/java/id/bubakangreen/app/data/fixture/DefaultLearningData.kt'
with open(out_path, 'w', encoding='utf-8') as f:
    f.write(code)

print(f'Generated {len(questions)} questions and {len(voices)} voices into {out_path}')
