package com.example.dbpractice.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dbpractice.entity.Contact

@Dao
interface ContactDao {
    @Upsert
    suspend fun upsertContact(contact: Contact)
    @Delete
    suspend fun deleteContact(contact: Contact)
    @Query("Select * from Contact ORDER BY firstName ASC")
     fun getContactOrderByFirstName():LiveData<List<Contact>>

    @Query("Select * from Contact ORDER BY lastName ASC")
    fun getContactOrderByLastName():LiveData<List<Contact>>

    @Query("Select * from Contact ORDER BY phoneNUmber ASC")
    fun getContactOrderByPhoneNUmber():LiveData<List<Contact>>
}

