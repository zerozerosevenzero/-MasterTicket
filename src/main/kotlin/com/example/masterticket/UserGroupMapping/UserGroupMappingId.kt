package com.example.masterticket.UserGroupMapping

import java.io.Serializable


data class UserGroupMappingId (
    val userGroupId: String,
    val userId: String,
): Serializable