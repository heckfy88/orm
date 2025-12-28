package org.orm.service

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.orm.domain.content.Module
import org.orm.domain.course.Course
import org.orm.domain.user.User
import org.orm.domain.user.UserRole
import org.orm.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuizServiceIntegrationTest {

    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    lateinit var moduleRepository: ModuleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var quizRepository: QuizRepository

    @Autowired
    lateinit var quizSubmissionRepository: QuizSubmissionRepository

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Test
    fun `createQuiz successfully creates a quiz`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val course = courseRepository.save(Course("Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))

        val quiz = quizService.createQuiz(module.id!!, "Quiz 1", 30)

        val loadedQuiz = quizRepository.findById(quiz.id!!).get()
        assertEquals("Quiz 1", loadedQuiz.title)
        assertEquals(30, loadedQuiz.timeLimit)
        assertEquals(module.id, loadedQuiz.module!!.id)
        assertEquals(quiz.id, moduleRepository.findById(module.id!!).get().quiz?.id)
    }

    @Test
    fun `createQuiz throws if module already has a quiz`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val course = courseRepository.save(Course("Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))

        quizService.createQuiz(module.id!!, "Quiz 1", 30)

        val ex = assertThrows(IllegalArgumentException::class.java) {
            quizService.createQuiz(module.id!!, "Quiz 2", 20)
        }
        assertEquals("Module already has a quiz", ex.message)
    }

    @Test
    fun `submitQuiz successfully submits a quiz`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))
        val quiz = quizService.createQuiz(module.id!!, "Quiz 1", 30)

        val submission = quizService.submitQuiz(quiz.id!!, student.id!!, 95)

        val loadedSubmission = quizSubmissionRepository.findById(submission.id!!).get()
        assertEquals(95, loadedSubmission.score)
        assertEquals(student.id, loadedSubmission.student.id)
        assertEquals(quiz.id, loadedSubmission.quiz.id)
    }

    @Test
    fun `submitQuiz throws if student already took quiz`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))
        val quiz = quizService.createQuiz(module.id!!, "Quiz 1", 30)

        quizService.submitQuiz(quiz.id!!, student.id!!, 90)

        val ex = assertThrows(IllegalArgumentException::class.java) {
            quizService.submitQuiz(quiz.id!!, student.id!!, 95)
        }
        assertEquals("Quiz already taken by this student", ex.message)
    }

    @Test
    fun `submitQuiz throws if score is out of range`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))
        val quiz = quizService.createQuiz(module.id!!, "Quiz 1", 30)

        val ex = assertThrows(IllegalArgumentException::class.java) {
            quizService.submitQuiz(quiz.id!!, student.id!!, 120)
        }
        assertEquals("Score must be between 0 and 100", ex.message)
    }
}