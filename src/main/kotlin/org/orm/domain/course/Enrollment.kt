package org.orm.domain.course

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import org.orm.domain.user.User
import java.time.Instant

@Entity
@Table(
    name = "enrollment",
    schema = "learning",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "course_id"])
    ]
)
class Enrollment(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var student: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    var course: Course,

    @Enumerated(EnumType.STRING)
    var status: EnrollmentStatus = EnrollmentStatus.ACTIVE,

    @Column(name = "enroll_date")
    var enrollDate: Instant? = null

) : BaseEntity()