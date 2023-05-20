package com.example.masterticket.UserGroupMapping

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@Entity
@IdClass(UserGroupMappingId::class) // 복합키 설정
class UserGroupMapping(
    var userGroupName: String?,
    var description: String?,
    @Id
    var userGroupId: String? = null,
    @Id
    var userId: String? = null,
) {
}