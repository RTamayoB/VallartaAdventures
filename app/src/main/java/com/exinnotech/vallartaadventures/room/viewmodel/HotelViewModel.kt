package com.exinnotech.vallartaadventures.room.viewmodel

import androidx.lifecycle.*
import com.exinnotech.vallartaadventures.room.entity.Hotel
import com.exinnotech.vallartaadventures.room.repository.HotelRepository
import kotlinx.coroutines.launch

/**
 * Hotel ViewModel to manage all contact with in the UI
 *
 * @property repository The hotel repository
 */
class HotelViewModel(private val repository: HotelRepository): ViewModel() {

    /**
     * Access the List trough the repository
     */
    val getHotelNames: LiveData<List<Hotel>> = repository.getHotelNames().asLiveData()

    /**
     * Insert a hotel via the repository
     *
     * @param hotel Hotel to insert
     */
    fun insert(hotel: Hotel) = viewModelScope.launch {
        repository.insert(hotel)
    }
}

/**
 * ViewModel Factory to instantiate the ViewModel
 *
 * @property repository The hotel repository
 */
class HotelViewModelFactory(private val repository: HotelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HotelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}