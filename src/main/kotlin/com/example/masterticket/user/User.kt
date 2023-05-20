package com.example.masterticket.user

import com.example.masterticket.BaseEntity
import javax.persistence.Entity

@Entity
class User(
    val userName: String,
    var status: UserStatus,
    val phone: String,
    val meta: Map<String, Object>,
) : BaseEntity() {

    fun getUuid(): String? {
        return if (meta.containsKey("uuid")) meta.get("uuid").toString() else null
    }
}