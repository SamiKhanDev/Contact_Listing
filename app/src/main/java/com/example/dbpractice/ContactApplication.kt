package com.example.dbpractice

import android.app.Application
import com.example.dbpractice.database.ContactDataBase

class ContactApplication:Application() {
    val dataBase: ContactDataBase by lazy { ContactDataBase.getDataBase(this)
    }
   lateinit var myName:String
}