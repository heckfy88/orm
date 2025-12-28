package org.orm.service

import org.orm.domain.course.Course
import org.orm.domain.course.Enrollment
import org.orm.domain.user.User
import org.orm.repository.CourseRepository
import org.orm.repository.EnrollmentRepository
import org.orm.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
) {

    fun enrollStudent(courseId: UUID, studentId: UUID): Enrollment {
        val student = userRepository.findById(studentId).orElseThrow { IllegalArgumentException("Student not found") }
        val course = courseRepository.findById(courseId).orElseThrow { IllegalArgumentException("Course not found") }

        require(
            !enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)
        ) { "Student is already enrolled in this course" }

        val enrollment = Enrollment(student = student, course = course)
        return enrollmentRepository.save(enrollment)
    }

    fun unenrollStudent(courseId: UUID, studentId: UUID) {
        val enrollment = enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, courseId)
            .orElseThrow { throw IllegalArgumentException("Enrollment not found") }
        enrollmentRepository.delete(enrollment)
    }

    fun getStudentCourses(studentId: UUID): List<Course> {
        userRepository.findById(studentId).orElseThrow { IllegalArgumentException("Student not found") }
        return enrollmentRepository.findByStudent_Id(studentId).map { it.course }
    }

    fun getCourseStudents(courseId: UUID): List<User> {
        courseRepository.findById(courseId).orElseThrow { IllegalArgumentException("Course not found") }
        return enrollmentRepository.findByCourse_Id(courseId).map { it.student }
    }
}