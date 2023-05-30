package com.example.masterticket.packaze

import com.example.masterticket.BaseEntity
import lombok.RequiredArgsConstructor
import javax.persistence.*

@Entity
class Package(

    val name: String,
    val count: Int? = 0,
    val period: Int?,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseEntity() {

}