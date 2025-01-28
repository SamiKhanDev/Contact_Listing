package com.example.dbpractice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.dbpractice.entity.Contact
import com.example.dbpractice.dao.ContactDao
import com.example.dbpractice.ContactEvent
import com.example.dbpractice.ContactState
import com.example.dbpractice.SortType
import kotlinx.coroutines.launch


//class ContactViewModel(private val dao:ContactDao):ViewModel() {
//    private val _sortType = MutableLiveData(SortType.FIRST_NAME)
//    private val _contacts: LiveData<List<Contact>> = Transformations.SwitchMap(_sortType) { sortType ->
//        when (sortType) {
//            SortType.FIRST_NAME -> dao.getContactOrderByFirstName()
//            SortType.LAST_NAME -> dao.getContactOrderByLastName()
//            SortType.PHONE_NUMBER -> dao.getContactOrderByPhoneNUmber()
//            else -> {
//
//            }
//        }
//    }
//
//    private val _state = MutableLiveData(ContactState())
//    val state: LiveData<ContactState> = combineLiveData(_state, _contacts, _sortType)
//
//    private fun combineLiveData(
//        state: LiveData<ContactState>,
//        contacts: LiveData<List<Contact>>,
//        sortType: LiveData<SortType>
//    ): LiveData<ContactState> {
//        return MediatorLiveData<ContactState>().apply {
//            fun update() {
//                val currentState = state.value ?: ContactState()
//                val currentContacts = contacts.value ?: emptyList()
//                val currentSortType = sortType.value ?: SortType.FIRST_NAME
//
//                value = currentState.copy(
//                    contacts = currentContacts,
//                    sortType = currentSortType
//                )
//            }
//
//            addSource(state) { update() }
//            addSource(contacts) { update() }
//            addSource(sortType) { update() }
//        }
//    }
////    @OptIn(ExperimentalCoroutinesApi::class)
////    private val _contacts = _sortType
////        .flatMapLatest {sortType->
////            when(sortType){
////                SortType.FIRST_NAME-> dao.getContactOrderByFirstName()
////                SortType.LAST_NAME-> dao.getContactOrderByLastName()
////                SortType.PHONE_NUMBER->dao.getContactOrderByPhoneNUmber()
////            }
////
////        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
////    private val _state = MutableStateFlow(ContactState())
////    val state = combine(_state,_sortType,_contacts){ state,sortType,contacts->
////        state.copy(
////            contacts =contacts,
////            sortType = sortType
////        )
////
////    }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000), ContactState())
//
//    fun onEvent(event: ContactEvent) {
//        when (event) {
//            is ContactEvent.DeleteContact -> {
//                viewModelScope.launch {
//                    dao.deleteContact(event.contact)
//                }
//            }
//
//            is ContactEvent.HideDialog -> {
//                _state.value = _state.value?.copy(isAddingContact = false)
//            }
//
//            is ContactEvent.ShowDialog -> {
//                _state.value = _state.value?.copy(isAddingContact = true)
//            }
//
//            is ContactEvent.SetFirstName -> {
//                _state.value = _state.value?.copy(firstName = event.firstName)
//            }
//
//            is ContactEvent.SetLastName -> {
//                _state.value = _state.value?.copy(lastName = event.lastName)
//            }
//
//            is ContactEvent.SetPhoneNumber -> {
//                _state.value = _state.value?.copy(phoneNumber = event.phoneNumber)
//            }
//
//            is ContactEvent.SortContacts -> {
//                _sortType.value = event.sortType
//            }
//
//            ContactEvent.SaveContact -> {
//                val currentState = _state.value ?: return
//                val firstName = currentState.firstName
//                val lastName = currentState.lastName
//                val phoneNumber = currentState.phoneNumber
//
//                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
//                    return
//                }
//
//                viewModelScope.launch {
//                    dao.upsertContact(
//                        Contact(
//                            firstName = firstName,
//                            lastName = lastName,
//                            phoneNUmber = phoneNumber
//                        )
//                    )
//                }
//
//                _state.value = currentState.copy(
//                    isAddingContact = false,
//                    firstName = "",
//                    lastName = "",
//                    phoneNumber = ""
//                )
//            }
//        }
//    }
//
//
//}
class ContactViewModel(private val dao: ContactDao) : ViewModel() {
    private val _sortType = MutableLiveData(SortType.FIRST_NAME)

    // LiveData that switches based on sort type
    private val contacts: LiveData<List<Contact>> = _sortType.switchMap { sortType ->
        when (sortType) {
            SortType.FIRST_NAME -> dao.getContactOrderByFirstName()
            SortType.LAST_NAME -> dao.getContactOrderByLastName()
            SortType.PHONE_NUMBER -> dao.getContactOrderByPhoneNUmber()
        }
    }

    private val _state = MutableLiveData(ContactState())

    // Combined state LiveData
    val state: LiveData<ContactState> = MediatorLiveData<ContactState>().apply {
        fun update() {
            val currentState = _state.value ?: ContactState()
            val currentContacts = contacts.value ?: emptyList()
            val currentSortType = _sortType.value ?: SortType.FIRST_NAME

            value = currentState.copy(
                contacts = currentContacts,
                sortType = currentSortType
            )
        }

        addSource(_state) { update() }
        addSource(contacts) { update() }
        addSource(_sortType) { update() }
    }

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }

            is ContactEvent.HideDialog -> {
                _state.value = _state.value?.copy(isAddingContact = false)
            }

            is ContactEvent.ShowDialog -> {
                _state.value = _state.value?.copy(isAddingContact = true)
            }

            is ContactEvent.SetFirstName -> {
                _state.value = _state.value?.copy(firstName = event.firstName)
            }

            is ContactEvent.SetLastName -> {
                _state.value = _state.value?.copy(lastName = event.lastName)
            }

            is ContactEvent.SetPhoneNumber -> {
                _state.value = _state.value?.copy(phoneNumber = event.phoneNumber)
            }

            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }

            ContactEvent.SaveContact -> {
                val currentState = _state.value ?: return
                if (currentState.firstName.isBlank() ||
                    currentState.lastName.isBlank() ||
                    currentState.phoneNumber.isBlank()) {
                    return
                }

                viewModelScope.launch {
                    dao.upsertContact(
                        Contact(
                            firstName = currentState.firstName,
                            lastName = currentState.lastName,
                            phoneNUmber = currentState.phoneNumber
                        )
                    )
                }

                _state.value = currentState.copy(
                    isAddingContact = false,
                    firstName = "",
                    lastName = "",
                    phoneNumber = ""
                )
            }
        }
    }
}