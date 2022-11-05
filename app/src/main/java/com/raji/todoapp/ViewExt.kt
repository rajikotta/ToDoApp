package com.raji.todoapp

import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener

inline fun SearchView.onQuerySubmit(crossinline onSubmit: (String) -> Unit) {
    this.setOnQueryTextListener(object : OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                onSubmit(query)
            }
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

    })
}