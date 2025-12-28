package org.orm.repository

import org.orm.domain.quiz.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuizRepository : JpaRepository<Quiz, UUID>