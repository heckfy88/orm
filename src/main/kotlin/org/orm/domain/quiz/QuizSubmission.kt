package org.orm.domain.quiz

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import org.orm.domain.user.User
import java.time.Instant


@Entity
@Table(
    name = "quiz_submission",
    schema = "learning",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["student_id", "quiz_id"])
    ]
)
class QuizSubmission(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false) var quiz: Quiz,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false) var student: User,

    var score: Int? = null,
    var takenAt: Instant? = null

) : BaseEntity()