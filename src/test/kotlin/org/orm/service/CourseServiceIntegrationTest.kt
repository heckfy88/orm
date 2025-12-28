package org.orm.service

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.orm.domain.course.Category
import org.orm.domain.course.Course
import org.orm.domain.user.User
import org.orm.domain.user.UserRole
import org.orm.repository.CategoryRepository
import org.orm.repository.CourseRepository
import org.orm.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseServiceIntegrationTest {

    @Autowired
    lateinit var courseService: CourseService

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var userRepository: UserRepository


    @Test
    fun `createCourse saves course with teacher and category`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val category = categoryRepository.save(Category("Math"))

        val course = courseService.createCourse(
            title = "Algebra 101",
            description = "Basic Algebra",
            categoryId = category.id,
            teacherId = teacher.id
        )

        val loadedCourse = courseRepository.findById(course.id!!).get()
        assertEquals("Algebra 101", loadedCourse.title)
        assertEquals("Basic Algebra", loadedCourse.description)
        assertEquals(teacher.id, loadedCourse.teacher!!.id)
        assertEquals(category.id, loadedCourse.category!!.id)
    }

    @Test
    fun `addModule adds module to course`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val course = courseRepository.save(Course(title = "Physics", teacher = teacher))

        val module = courseService.addModule(course.id!!, "Module 1", "Module description")

        val loadedCourse = courseRepository.findById(course.id!!).get()
        assertEquals(1, loadedCourse.modules.size)
        assertEquals(module.id, loadedCourse.modules[0].id)
        assertEquals("Module 1", loadedCourse.modules[0].title)
    }

    @Test
    fun `updateCourse modifies existing course`() {
        val teacher1 = userRepository.save(User("Teacher1", "t1@test.com", UserRole.TEACHER))
        val teacher2 = userRepository.save(User("Teacher2", "t2@test.com", UserRole.TEACHER))
        val course = courseRepository.save(Course(title = "Old Title", teacher = teacher1))

        courseService.updateCourse(course.id!!, "New Title", "Updated description", teacher2.id)

        val updatedCourse = courseRepository.findById(course.id!!).get()
        assertEquals("New Title", updatedCourse.title)
        assertEquals("Updated description", updatedCourse.description)
        assertEquals(teacher2.id, updatedCourse.teacher!!.id)
    }

    @Test
    fun `deleteCourse removes course`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val course = courseRepository.save(Course(title = "Course to delete", teacher = teacher))

        courseService.deleteCourse(course.id!!)

        assertFalse(courseRepository.existsById(course.id!!))
    }

    @Test
    fun `getCourse returns existing course`() {
        val teacher = userRepository.save(User("Teacher", "teacher@test.com", UserRole.TEACHER))
        val course = courseRepository.save(Course(title = "Course", teacher = teacher))

        val loaded = courseService.getCourse(course.id!!)

        assertEquals(course.id, loaded.id)
        assertEquals("Course", loaded.title)
        assertEquals(teacher.id, loaded.teacher!!.id)
    }
}