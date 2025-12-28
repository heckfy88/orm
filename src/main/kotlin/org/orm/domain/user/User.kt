package org.orm.domain.user

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import org.orm.domain.course.Course
import org.orm.domain.course.Enrollment

@Entity
@Table(name = "user", schema = "learning")
class User(

    @Column(nullable = false) var name: String,

    @Column(nullable = false, unique = true) var email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) var role: UserRole

) : BaseEntity() {

    @OneToOne(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var profile: UserProfile? = null

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    var coursesTaught: MutableList<Course> = mutableListOf()

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    var enrollments: MutableList<Enrollment> = mutableListOf()
}