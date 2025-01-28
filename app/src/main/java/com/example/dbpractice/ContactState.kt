package com.example.dbpractice

import com.example.dbpractice.entity.Contact

data class ContactState(
    val contacts:List<Contact> = emptyList(),
    val firstName: String="",
    val lastName: String="",
    val phoneNumber: String="",
    val isAddingContact:Boolean=false,
    val sortType: SortType=SortType.FIRST_NAME
)
