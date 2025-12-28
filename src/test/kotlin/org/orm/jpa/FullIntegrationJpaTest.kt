package org.orm.jpa

import org.junit.jupiter.api.Test
import org.orm.domain.content.Assignment
import org.orm.domain.content.Lesson
import org.orm.domain.course.Course
import org.orm.domain.user.User
import org.orm.domain.user.UserRole
import org.orm.repository.AssignmentRepository
import org.orm.repository.CourseRepository
import org.orm.repository.ModuleRepository
import org.orm.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import org.orm.domain.content.Module
import org.orm.domain.content.Submission
import org.orm.domain.course.Enrollment
import org.orm.domain.quiz.AnswerOption
import org.orm.domain.quiz.Question
import org.orm.domain.quiz.QuestionType
import org.orm.domain.quiz.Quiz
import org.orm.domain.quiz.QuizSubmission
import org.orm.repository.EnrollmentRepository
import org.orm.repository.LessonRepository
import org.orm.repository.QuizRepository
import org.orm.repository.QuizSubmissionRepository
import org.orm.repository.SubmissionRepository
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = ["/sql/test.sql"])
class FullIntegrationJPATest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var moduleRepository: ModuleRepository

    @Autowired
    lateinit var lessonRepository: LessonRepository

    @Autowired
    lateinit var assignmentRepository: AssignmentRepository

    @Autowired
    lateinit var submissionRepository: SubmissionRepository

    @Autowired
    lateinit var enrollmentRepository: EnrollmentRepository

    @Autowired
    lateinit var quizRepository: QuizRepository

    @Autowired
    lateinit var quizSubmissionRepository: QuizSubmissionRepository

    @Test
    fun `full course lifecycle with cascades`() {
        // --- Пользователи ---
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))

        // --- Курс и модули ---
        val course = Course(title = "Full Course", teacher = teacher)
        val module1 = Module(course = course, title = "Module 1")
        val module2 = Module(course = course, title = "Module 2")
        course.modules.addAll(listOf(module1, module2))

        // --- Уроки ---
        val lesson1 = Lesson(module1, "Lesson 1")
        val lesson2 = Lesson(module2, "Lesson 2")
        module1.lessons.add(lesson1)
        module2.lessons.add(lesson2)

        // --- Задания и submissions ---
        val assignment1 = Assignment(lesson1, "Assignment 1")
        lesson1.assignments.add(assignment1)

        val submission1 = Submission(assignment1, student, score = 95)
        assignment1.submissions.add(submission1)

        // --- Сохраняем курс, все должно каскадно сохраниться ---
        val savedCourse = courseRepository.save(course)

        // --- Проверки ---
        val loadedCourse = courseRepository.findById(savedCourse.id!!).get()
        assertEquals(2, loadedCourse.modules.size)
        assertEquals(1, loadedCourse.modules[0].lessons.size)
        assertEquals("Lesson 1", loadedCourse.modules[0].lessons[0].title)

        val loadedAssignment = assignmentRepository.findById(assignment1.id!!).get()
        assertEquals(1, loadedAssignment.submissions.size)
        assertEquals(95, loadedAssignment.submissions[0]!!.score)

        // --- Запись на курс ---
        val enrollment = Enrollment(student, savedCourse)
        enrollmentRepository.save(enrollment)
        assertEquals(1, enrollmentRepository.findAll().size)

        // --- Викторины ---
        val quiz = Quiz(module1, "Quiz 1", timeLimit = 30)
        val question1 = Question(quiz, "Q1?", QuestionType.SINGLE_CHOICE)
        val option1 = AnswerOption(question1, "Option 1", true)
        val option2 = AnswerOption(question1, "Option 2", false)
        question1.options.addAll(listOf(option1, option2))
        quiz.questions.add(question1)
        quizRepository.save(quiz)

        val quizSubmission = QuizSubmission(quiz, student, score = 100)
        quizSubmissionRepository.save(quizSubmission)

        val loadedQuiz = quizRepository.findById(quiz.id!!).get()
        assertEquals(1, loadedQuiz.questions.size)
        assertEquals(2, loadedQuiz.questions[0].options.size)
        assertEquals(100, quizSubmissionRepository.findById(quizSubmission.id!!).get().score)
    }

    @Test
    fun `delete lesson cascades assignments and submissions`() {
        // --- Создание пользователей ---
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))

        // --- Создание курса и модуля ---
        val course = courseRepository.save(Course(title = "Course", teacher = teacher))
        val module = moduleRepository.save(Module(course = course, title = "Module"))

        // --- Создание урока ---
        val lesson = lessonRepository.save(Lesson(module = module, title = "Lesson"))

        val assignment = Assignment(title = "Assignment", lesson = lesson)
        lesson.assignments.add(assignment)

        val submission = Submission(assignment = assignment, student = student, score = 90)
        assignment.submissions.add(submission)

        // Сохраняем только Lesson — cascade сохранит Assignment и Submission
        lessonRepository.save(lesson)
        // --- Sanity check ---
        assertEquals(1, lessonRepository.count())
        assertEquals(1, assignmentRepository.count())
        assertEquals(1, submissionRepository.count())

        // --- Удаление урока, проверка каскадов ---
        lessonRepository.delete(lesson)
        lessonRepository.flush()

        assertEquals(0, lessonRepository.count())
        assertEquals(0, assignmentRepository.count())
        assertEquals(0, submissionRepository.count()) // теперь удалятся автоматически
    }
}

