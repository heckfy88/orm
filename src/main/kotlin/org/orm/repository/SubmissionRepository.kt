package org.orm.repository

import org.orm.domain.content.Assignment
import org.orm.domain.content.Submission
import org.orm.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SubmissionRepository : JpaRepository<Submission, UUID> {
    fun findByStudent_Id(studentId: UUID): List<Submission>
    fun findByAssignment_Id(assignmentId: UUID): List<Submission>
    fun existsByAssignmentAndStudent(assignment: Assignment, student: User): Boolean
}