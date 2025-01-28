package com.example.dbpractice.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dbpractice.dao.ContactDao
import com.example.dbpractice.entity.Contact

@Database(
    entities = [Contact::class],
    version = 1
)

abstract class ContactDataBase:RoomDatabase() {
    abstract val dao: ContactDao

    companion object{
        @Volatile
        private var INSTANCE: ContactDataBase?=null

        fun getDataBase(context: Context): ContactDataBase {
            return INSTANCE ?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    ContactDataBase::class.java,
                    "contact_database"
                ).build()
                INSTANCE =instance
                instance
            }
        }
    }

}