package com.example.dbpractice.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Contact(
    val firstName:String,
    val lastName:String,
    val phoneNUmber:String,
    @PrimaryKey(autoGenerate = true)
val id :Int=0
)
