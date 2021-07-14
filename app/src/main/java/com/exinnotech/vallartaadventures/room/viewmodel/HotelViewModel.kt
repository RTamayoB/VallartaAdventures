package com.exinnotech.vallartaadventures.room.viewmodel

import androidx.lifecycle.*
import com.exinnotech.vallartaadventures.room.entity.Hotel
import com.exinnotech.vallartaadventures.room.repository.HotelRepository
import kotlinx.coroutines.launch

class HotelViewModel(private val repository: HotelRepository): ViewModel() {

    val getHotelNames: LiveData<List<Hotel>> = repository.getHotelNames().asLiveData()

    fun insert(hotel: Hotel) = viewModelScope.launch {
        repository.insert(hotel)
    }
}

class HotelViewModelFactory(private val repository: HotelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HotelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}