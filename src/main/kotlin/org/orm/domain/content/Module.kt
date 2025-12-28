package org.orm.domain.content

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import org.orm.domain.course.Course
import org.orm.domain.quiz.Quiz

@Entity
@Table(name = "module", schema = "learning")
class Module(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false) var course: Course,

    @Column(nullable = false) var title: String,

    var description: String? = null,
    var orderIndex: Int? = null

) : BaseEntity() {

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var lessons: MutableList<Lesson> = mutableListOf()

    @OneToOne(mappedBy = "module")
    var quiz: Quiz? = null
}