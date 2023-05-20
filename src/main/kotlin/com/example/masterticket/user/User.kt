package com.example.masterticket.user

import com.example.masterticket.BaseEntity
import org.hibernate.annotations.Type
import javax.persistence.*

@Entity
class User(
    val userName: String,
    var status: UserStatus,
    val phone: String,
    @Type(type = "json")
    @Column(columnDefinition = "json")
    var meta: Map<String, Any>? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {

    val uuid: String?
        get() {
            var uuid: String? = null
            if (meta?.containsKey("uuid") == true) { uuid = meta?.get("uuid").toString() }
            return uuid
        }
}