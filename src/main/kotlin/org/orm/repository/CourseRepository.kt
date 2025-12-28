package org.orm.repository

import org.orm.domain.course.Course
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CourseRepository : JpaRepository<Course, UUID> {

    fun findByCategory_Id(categoryId: UUID): List<Course>

    @EntityGraph(attributePaths = ["modules", "modules.lessons"])
    fun findDetailedById(id: UUID): Course?
}