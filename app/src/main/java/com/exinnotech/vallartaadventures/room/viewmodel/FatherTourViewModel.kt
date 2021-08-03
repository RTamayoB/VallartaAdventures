package com.exinnotech.vallartaadventures.room.viewmodel

import androidx.lifecycle.*
import com.exinnotech.vallartaadventures.room.entity.FatherTour
import com.exinnotech.vallartaadventures.room.repository.FatherTourRepository
import kotlinx.coroutines.launch

/**
 * FatherTour ViewModel to manage all contact with in the UI
 *
 * @property repository The father tour repository
 */
class FatherTourViewModel(private val repository: FatherTourRepository) : ViewModel(){

    /**
     * Access the List trough the repository
     */
    val getFatherTourNames: LiveData<List<FatherTour>> = repository.getFatherTourNames().asLiveData()

    /**
     * Insert a father tour via the repository
     *
     * @param fatherTour FatherTour to insert
     */
    fun insert(fatherTour: FatherTour) = viewModelScope.launch {
        repository.insert(fatherTour)
    }
}

/**
 * ViewModel Factory to instantiate the ViewModel
 *
 * @property repository The father tour repository
 */
class FatherTourViewModelFactory(private val repository: FatherTourRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FatherTourViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FatherTourViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}