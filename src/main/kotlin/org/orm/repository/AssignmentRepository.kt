package org.orm.repository

import org.orm.domain.content.Assignment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AssignmentRepository : JpaRepository<Assignment, UUID> {
    fun findByLesson_Id(lessonId: UUID): List<Assignment>
}