package com.example.masterticket.UserGroupMapping

import com.example.masterticket.BaseEntity
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@Entity
@IdClass(UserGroupMappingId::class) // 복합키 설정
class UserGroupMapping(
    var userGroupName: String? = null,
    var description: String? = null,
    @Id
    var userGroupId: String? = null,
    @Id
    var userId: String? = null,
): BaseEntity() {
}