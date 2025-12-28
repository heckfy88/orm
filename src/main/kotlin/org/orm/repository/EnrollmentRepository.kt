package org.orm.repository

import org.orm.domain.course.Enrollment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EnrollmentRepository : JpaRepository<Enrollment, UUID> {
    fun findByStudent_Id(studentId: UUID): List<Enrollment>
    fun findByCourse_Id(courseId: UUID): List<Enrollment>
    fun existsByStudent_IdAndCourse_Id(studentId: UUID, courseId: UUID): Boolean
    fun findByStudent_IdAndCourse_Id(studentId: UUID, courseId: UUID): Optional<Enrollment>
}