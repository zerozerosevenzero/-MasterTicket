package com.example.masterticket.packaze

import com.example.masterticket.BaseEntity
import javax.persistence.*

@Entity
class Package(

    val name: String,
    val count: Integer,
    val period: Integer,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseEntity() {

}