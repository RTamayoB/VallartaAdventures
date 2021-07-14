package com.exinnotech.vallartaadventures.room.viewmodel

import androidx.lifecycle.*
import com.exinnotech.vallartaadventures.room.entity.Tour
import com.exinnotech.vallartaadventures.room.repository.TourRepository
import kotlinx.coroutines.launch

class TourViewModel(private val repository: TourRepository): ViewModel() {

    val getTourNames: LiveData<List<Tour>> = repository.getTourNames().asLiveData()

    fun insert(tour: Tour) = viewModelScope.launch {
        repository.insert(tour)
    }
}

class TourViewModelFactory(private val repository: TourRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}