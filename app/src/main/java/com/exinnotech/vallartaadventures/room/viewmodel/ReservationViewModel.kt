package com.exinnotech.vallartaadventures.room.viewmodel

import androidx.lifecycle.*
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.exinnotech.vallartaadventures.room.repository.ReservationRepository
import kotlinx.coroutines.launch

class ReservationViewModel(private val repository: ReservationRepository): ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val getReservations: LiveData<List<Reservation>> = repository.getReservations().asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(reservation: Reservation) = viewModelScope.launch {
         repository.insert(reservation)
    }

    fun getReservationsByConfNum(confNum: String): LiveData<Reservation> {
        return repository.getReservationByConfNum(confNum)
    }
}

class ReservationViewModelFactory(private val repository: ReservationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}