package com.example.masterticket.UserGroupMapping

import lombok.NoArgsConstructor
import java.io.Serializable

data class UserGroupMappingId (
    var userGroupId: String,
    var userId: String,
): Serializable {
    constructor() : this("", "")
}