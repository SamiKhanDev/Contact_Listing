package com.example.dbpractice

import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dbpractice.adapter.ContactAdapter
import com.example.dbpractice.databinding.ActivityMainBinding
import com.example.dbpractice.viewmodel.ContactViewModel
import com.example.dbpractice.viewmodel.ContactViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((application as ContactApplication).dataBase.dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
         binding.recyclerViewContacts.apply {

            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ContactAdapter { contact ->
                viewModel.onEvent(ContactEvent.DeleteContact(contact))
            }
        }

    }

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            (binding.recyclerViewContacts.adapter as ContactAdapter).submitList(state.contacts)
            updateSortUI(state.sortType)

            if (state.isAddingContact) showAddContactDialog()
        }
    }

    private fun setupListeners() {
        binding.fabAddContact.setOnClickListener {
            viewModel.onEvent(ContactEvent.ShowDialog)
        }
        mapOf(
            binding.rdSortFirstname to SortType.FIRST_NAME,
            binding.rdSortLastname to SortType.LAST_NAME,
            binding.rdSortPhone to SortType.PHONE_NUMBER
        ).forEach { (button, sortType) ->
            button.setOnClickListener {
                viewModel.onEvent(ContactEvent.SortContacts(sortType))
            }
        }
    }

    private fun updateSortUI(sortType: SortType) {
        val checkedId = when (sortType) {
            SortType.FIRST_NAME -> R.id.rd_sort_firstname
            SortType.LAST_NAME -> R.id.rd_sort_lastname
            SortType.PHONE_NUMBER -> R.id.rd_sort_phone
        }
        binding.radioGroup.check(checkedId)
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val editFirstName = dialogView.findViewById<EditText>(R.id.edit_first_name)
        val editLastName = dialogView.findViewById<EditText>(R.id.edit_last_name)
        val editPhone = dialogView.findViewById<EditText>(R.id.edit_phone)

        AlertDialog.Builder(this)
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                viewModel.onEvent(ContactEvent.SetFirstName(editFirstName.text.toString()))
                viewModel.onEvent(ContactEvent.SetLastName(editLastName.text.toString()))
                viewModel.onEvent(ContactEvent.SetPhoneNumber(editPhone.text.toString()))
                viewModel.onEvent(ContactEvent.SaveContact)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                viewModel.onEvent(ContactEvent.HideDialog)
                dialog.dismiss()
            }
            .setOnDismissListener {
                viewModel.onEvent(ContactEvent.HideDialog)
            }
            .show()
    }
}