package com.example.apassignment.ui.grid

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apassignment.data.repository.MediaRepository
import com.example.apassignment.domain.model.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImageGridViewModel(
    private val repository: MediaRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<MediaItem>>(emptyList())
    val items: StateFlow<List<MediaItem>> = _items

    private var page = 0
    private val pageSize = 50
    private var loading = false


    init {
        loadNextPage()
    }
    fun loadNextPage() {


        if (loading) return
        loading = true

        viewModelScope.launch {
            val newItems = repository.loadPage(page, pageSize)
            _items.value += newItems
            page++
            loading = false
        }
    }
}
