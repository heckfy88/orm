package org.orm.domain.user

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity

@Entity
@Table(name = "user_profile", schema = "learning")
class UserProfile(

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) var user: User,

    var bio: String? = null

) : BaseEntity()