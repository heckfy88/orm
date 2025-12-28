package org.orm.service

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.orm.domain.course.Course
import org.orm.domain.user.User
import org.orm.domain.user.UserRole
import org.orm.repository.CourseRepository
import org.orm.repository.EnrollmentRepository
import org.orm.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnrollmentServiceIntegrationTest {

    @Autowired
    lateinit var enrollmentService: EnrollmentService

    @Autowired
    lateinit var enrollmentRepository: EnrollmentRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Test
    fun `enrollStudent saves enrollment`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))

        val enrollment = enrollmentService.enrollStudent(course.id!!, student.id!!)
        assertNotNull(enrollment.id)
        assertEquals(student.id, enrollment.student.id)
        assertEquals(course.id, enrollment.course.id)
        assertEquals(1, enrollmentRepository.count())
    }

    @Test
    fun `enrollStudent throws when already enrolled`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))

        enrollmentService.enrollStudent(course.id!!, student.id!!)

        val ex = assertThrows(IllegalArgumentException::class.java) {
            enrollmentService.enrollStudent(course.id!!, student.id!!)
        }
        assertEquals("Student is already enrolled in this course", ex.message)
    }

    @Test
    fun `unenrollStudent deletes enrollment`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))

        val enrollment = enrollmentService.enrollStudent(course.id!!, student.id!!)
        enrollmentService.unenrollStudent(course.id!!, student.id!!)

        assertEquals(0, enrollmentRepository.count())
    }

    @Test
    fun `getStudentCourses returns courses of student`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student = userRepository.save(User("Student", "student@test.com", UserRole.STUDENT))
        val course1 = courseRepository.save(Course("Course 1", teacher = teacher))
        val course2 = courseRepository.save(Course("Course 2", teacher = teacher))

        enrollmentService.enrollStudent(course1.id!!, student.id!!)
        enrollmentService.enrollStudent(course2.id!!, student.id!!)

        val courses = enrollmentService.getStudentCourses(student.id!!)
        assertEquals(2, courses.size)
        assertTrue(courses.any { it.id == course1.id })
        assertTrue(courses.any { it.id == course2.id })
    }

    @Test
    fun `getCourseStudents returns students of course`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val student1 = userRepository.save(User("Student1", "s1@test.com", UserRole.STUDENT))
        val student2 = userRepository.save(User("Student2", "s2@test.com", UserRole.STUDENT))
        val course = courseRepository.save(Course("Course", teacher = teacher))

        enrollmentService.enrollStudent(course.id!!, student1.id!!)
        enrollmentService.enrollStudent(course.id!!, student2.id!!)

        val students = enrollmentService.getCourseStudents(course.id!!)
        assertEquals(2, students.size)
        assertTrue(students.any { it.id == student1.id })
        assertTrue(students.any { it.id == student2.id })
    }
}