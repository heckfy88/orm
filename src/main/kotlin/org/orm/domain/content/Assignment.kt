package org.orm.domain.content

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import java.time.Instant

@Entity
@Table(name = "assignment", schema = "learning")
class Assignment(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    val lesson: Lesson? = null,

    @Column(nullable = false)
    val title: String? = null,

    val description: String? = null,

    var dueDate: Instant? = null,

    @Column(name = "max_score")
    private val maxScore: Int? = null,
) : BaseEntity() {
    @OneToMany(mappedBy = "assignment", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val submissions: MutableList<Submission?> = ArrayList<Submission?>()
}