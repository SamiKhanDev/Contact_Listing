package com.example.dbpractice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dbpractice.dao.ContactDao
import java.lang.IllegalArgumentException

class ContactViewModelFactory(private val dao: ContactDao):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(dao) as T
        }
        throw IllegalArgumentException("unKnown ViewModel class")
    }
}