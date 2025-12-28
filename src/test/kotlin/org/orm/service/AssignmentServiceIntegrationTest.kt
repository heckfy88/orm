package org.orm.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.orm.domain.content.Lesson
import org.orm.domain.content.Module
import org.orm.domain.course.Course
import org.orm.domain.user.User
import org.orm.domain.user.UserRole
import org.orm.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AssignmentServiceIntegrationTest {

    @Autowired
    lateinit var assignmentService: AssignmentService

    @Autowired
    lateinit var lessonRepository: LessonRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var moduleRepository: ModuleRepository

    @Autowired
    lateinit var assignmentRepository: AssignmentRepository

    @Autowired
    lateinit var submissionRepository: SubmissionRepository

    @Test
    fun `createAssignment saves assignment and links to lesson`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val course = courseRepository.save(Course(title = "Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))
        val lesson = lessonRepository.save(Lesson(module = module, title = "Lesson"))

        val assignment = assignmentService.createAssignment(
            lessonId = lesson.id!!,
            title = "Test Assignment",
            dueDate = Instant.now()
        )

        // Проверяем сохранение
        val loadedAssignment = assignmentRepository.findById(assignment.id!!).get()
        assertEquals("Test Assignment", loadedAssignment.title)
        assertEquals(lesson.id, loadedAssignment.lesson!!.id)

        // Проверяем, что assignment добавлен в урок
        val loadedLesson = lessonRepository.findById(lesson.id!!).get()
        assertTrue(loadedLesson.assignments.any { it.id == assignment.id })
    }

    @Test
    fun `submitAssignment saves submission and prevents duplicate`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course(title = "Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))
        val lesson = lessonRepository.save(Lesson(module, "Lesson"))
        val assignment = assignmentService.createAssignment(lesson.id!!, "Assignment 1", Instant.now())

        val submission = assignmentService.submitAssignment(assignment.id!!, student.id!!, 90)

        // Проверка сохранения
        val loadedSubmission = submissionRepository.findById(submission.id!!).get()
        assertEquals(90, loadedSubmission.score)
        assertEquals(assignment.id, loadedSubmission.assignment.id)
        assertEquals(student.id, loadedSubmission.student.id)
    }

    @Test
    fun `gradeSubmission updates score`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))
        val module = moduleRepository.save(Module(course, "Module"))
        val lesson = lessonRepository.save(Lesson(module, "Lesson"))
        val assignment = assignmentService.createAssignment(lesson.id!!, "Assignment 1", Instant.now())
        val submission = assignmentService.submitAssignment(assignment.id!!, student.id!!, null)

        assignmentService.gradeSubmission(submission.id!!, 85)

        val loadedSubmission = submissionRepository.findById(submission.id!!).get()
        assertEquals(85, loadedSubmission.score)
    }

    @Test
    fun `getAssignmentSubmissions returns all submissions`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student1 = userRepository.save(User("Student1", "s1@test.com", UserRole.STUDENT))
        val student2 = userRepository.save(User("Student2", "s2@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))
        val module = moduleRepository.save(Module(course, "Module"))
        val lesson = lessonRepository.save(Lesson(module, "Lesson"))
        val assignment = assignmentService.createAssignment(lesson.id!!, "Assignment 1", Instant.now())

        val sub1 = assignmentService.submitAssignment(assignment.id!!, student1.id!!, 90)
        val sub2 = assignmentService.submitAssignment(assignment.id!!, student2.id!!, 95)

        val submissions = assignmentService.getAssignmentSubmissions(assignment.id!!)
        assertEquals(2, submissions.size)
        assertTrue(submissions.any { it.id == sub1.id })
        assertTrue(submissions.any { it.id == sub2.id })
    }
}