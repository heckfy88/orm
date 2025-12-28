package org.orm.service

import org.orm.domain.quiz.Quiz
import org.orm.domain.quiz.QuizSubmission
import org.orm.repository.ModuleRepository
import org.orm.repository.QuizRepository
import org.orm.repository.QuizSubmissionRepository
import org.orm.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
@Transactional
class QuizService(
    private val quizRepository: QuizRepository,
    private val quizSubmissionRepository: QuizSubmissionRepository,
    private val moduleRepository: ModuleRepository,
    private val userRepository: UserRepository
) {

    fun createQuiz(moduleId: UUID, title: String, timeLimit: Int?): Quiz {
        require(title.isNotBlank()) { "Quiz title cannot be blank" }
        val module = moduleRepository.findById(moduleId).orElseThrow { IllegalArgumentException("Module not found") }

        require(module.quiz == null) { "Module already has a quiz" } // проверка 1-1 связи

        val quiz = Quiz(module = module, title = title, timeLimit = timeLimit)
        module.quiz = quiz
        return quizRepository.save(quiz)
    }

    fun submitQuiz(quizId: UUID, studentId: UUID, score: Int): QuizSubmission {
        val quiz = quizRepository.findById(quizId).orElseThrow { IllegalArgumentException("Quiz not found") }
        val student = userRepository.findById(studentId).orElseThrow { IllegalArgumentException("Student not found") }

        require(
            !quizSubmissionRepository.existsByQuizAndStudent(quiz, student)
        ) { "Quiz already taken by this student" }
        require(score in 0..100) { "Score must be between 0 and 100" }

        val submission = QuizSubmission(quiz = quiz, student = student, score = score, takenAt = Instant.now())
        return quizSubmissionRepository.save(submission)
    }
}