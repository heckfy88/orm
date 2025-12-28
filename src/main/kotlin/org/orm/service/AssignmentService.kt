package org.orm.service

import org.orm.domain.content.Assignment
import org.orm.domain.content.Submission
import org.orm.repository.AssignmentRepository
import org.orm.repository.LessonRepository
import org.orm.repository.SubmissionRepository
import org.orm.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
@Transactional
class AssignmentService(
    private val assignmentRepository: AssignmentRepository,
    private val submissionRepository: SubmissionRepository,
    private val lessonRepository: LessonRepository,
    private val userRepository: UserRepository
) {

    fun createAssignment(lessonId: UUID, title: String, dueDate: Instant?): Assignment {
        require(title.isNotBlank()) { "Assignment title cannot be blank" }
        val lesson = lessonRepository.findById(lessonId).orElseThrow { IllegalArgumentException("Lesson not found") }
        val assignment = Assignment(lesson = lesson, title = title, dueDate = dueDate)
        lesson.assignments.add(assignment)
        return assignmentRepository.save(assignment)
    }

    fun submitAssignment(assignmentId: UUID, studentId: UUID, score: Int?): Submission {
        val assignment =
            assignmentRepository.findById(assignmentId).orElseThrow { IllegalArgumentException("Assignment not found") }
        val student = userRepository.findById(studentId).orElseThrow { IllegalArgumentException("Student not found") }

        require(
            !submissionRepository.existsByAssignmentAndStudent(
                assignment,
                student
            )
        ) { "Assignment already submitted by this student" }

        val submission = Submission(assignment = assignment, student = student, score = score)
        return submissionRepository.save(submission)
    }

    fun gradeSubmission(submissionId: UUID, score: Int) {
        val submission =
            submissionRepository.findById(submissionId).orElseThrow { IllegalArgumentException("Submission not found") }
        require(score in 0..100) { "Score must be between 0 and 100" }
        submission.score = score
        submissionRepository.save(submission)
    }

    fun getAssignmentSubmissions(assignmentId: UUID): List<Submission> {
        check(assignmentRepository.existsById(assignmentId)) { "Assignment not found" }
        return submissionRepository.findByAssignment_Id(assignmentId)
    }
}