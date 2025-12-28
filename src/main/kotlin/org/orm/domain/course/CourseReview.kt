package org.orm.domain.course

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import org.orm.domain.user.User

@Entity
@Table(
    name = "course_review",
    schema = "learning",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["student_id", "course_id"])
    ]
)
class CourseReview(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false) var course: Course,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false) var student: User,

    @Column(nullable = false) var rating: Int,

    var comment: String? = null

) : BaseEntity()