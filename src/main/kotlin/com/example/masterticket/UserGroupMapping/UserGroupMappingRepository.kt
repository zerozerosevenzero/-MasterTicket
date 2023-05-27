package com.example.masterticket.UserGroupMapping

import org.springframework.data.jpa.repository.JpaRepository

interface UserGroupMappingRepository : JpaRepository<UserGroupMapping, Integer> {
    fun findByUserGroupId(userGroupId: String): List<UserGroupMapping>
}