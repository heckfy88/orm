package org.orm.repository

import org.orm.domain.content.Lesson
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LessonRepository : JpaRepository<Lesson, UUID> {
    fun findByModule_Id(moduleId: UUID): List<Lesson>
}