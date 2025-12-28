package org.orm.service


import org.orm.domain.content.Module
import org.orm.domain.course.Course
import org.orm.repository.CategoryRepository
import org.orm.repository.CourseRepository
import org.orm.repository.ModuleRepository
import org.orm.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class CourseService(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val moduleRepository: ModuleRepository
) {

    fun createCourse(title: String, description: String?, categoryId: UUID?, teacherId: UUID?): Course {
        require(title.isNotBlank()) { "Course title cannot be blank" }

        val category = categoryId?.let {
            categoryRepository.findById(it).orElseThrow { IllegalArgumentException("Category not found") }
        }
        val teacher =
            teacherId?.let { userRepository.findById(it).orElseThrow { IllegalArgumentException("Teacher not found") } }

        val course = Course(title = title, description = description, category = category, teacher = teacher)
        return courseRepository.save(course)
    }

    fun addModule(courseId: UUID, title: String, description: String?): Module {
        require(title.isNotBlank()) { "Module title cannot be blank" }
        val course = courseRepository.findById(courseId).orElseThrow { IllegalArgumentException("Course not found") }

        val module = Module(course = course, title = title, description = description)
        course.modules.add(module)
        return moduleRepository.save(module)
    }

    fun updateCourse(courseId: UUID, title: String?, description: String?, teacherId: UUID?) {
        val course = courseRepository.findById(courseId).orElseThrow { IllegalArgumentException("Course not found") }
        title?.let { require(it.isNotBlank()) { "Course title cannot be blank" }; course.title = it }
        description?.let { course.description = it }
        teacherId?.let {
            course.teacher = userRepository.findById(it).orElseThrow { IllegalArgumentException("Teacher not found") }
        }
        courseRepository.save(course)
    }

    fun deleteCourse(courseId: UUID) {
        check(courseRepository.existsById(courseId)) { "Course not found" }
        courseRepository.deleteById(courseId)
    }

    fun getCourse(courseId: UUID): Course =
        courseRepository.findById(courseId).orElseThrow { IllegalArgumentException("Course not found") }
}