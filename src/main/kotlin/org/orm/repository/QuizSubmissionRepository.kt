package org.orm.repository

import org.orm.domain.quiz.Quiz
import org.orm.domain.quiz.QuizSubmission
import org.orm.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuizSubmissionRepository : JpaRepository<QuizSubmission, UUID> {
    fun findByStudent_Id(studentId: UUID): List<QuizSubmission>
    fun findByQuiz_Id(quizId: UUID): List<QuizSubmission>
    fun existsByQuizAndStudent(quiz: Quiz, student: User): Boolean
}