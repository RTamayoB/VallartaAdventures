package com.exinnotech.vallartaadventures.room.viewmodel

import androidx.lifecycle.*
import com.exinnotech.vallartaadventures.room.entity.Tour
import com.exinnotech.vallartaadventures.room.repository.TourRepository
import kotlinx.coroutines.launch

/**
 * Tour ViewModel to manage all contact with in the UI
 *
 * @property repository The tour repository
 */
class TourViewModel(private val repository: TourRepository): ViewModel() {

    /**
     * Access the List trough the repository
     */
    val getTourNames: LiveData<List<Tour>> = repository.getTourNames().asLiveData()

    /**
     * Insert a tour via the repository
     *
     * @param tour Tour to insert
     */
    fun insert(tour: Tour) = viewModelScope.launch {
        repository.insert(tour)
    }
}

/**
 * ViewModel Factory to instantiate the ViewModel
 *
 * @property repository The tour repository
 */
class TourViewModelFactory(private val repository: TourRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}