package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.CarOwnerHomeResponse
import com.mobile.garaje.data.repository.CarOwnerHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CarOwnerHomeState {
    object Loading                                     : CarOwnerHomeState()
    data class Error(val message: String)              : CarOwnerHomeState()
    data class Success(val data: CarOwnerHomeResponse) : CarOwnerHomeState()
}

class CarOwnerHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CarOwnerHomeRepository()
    private val prefs      = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun token()    = prefs.getString("token", "") ?: ""

    private val _state = MutableStateFlow<CarOwnerHomeState>(CarOwnerHomeState.Loading)
    val state: StateFlow<CarOwnerHomeState> = _state

    init { loadHome() }

    // ── All vehicles — called on home screen launch ───────────────────────────
    fun loadHome() {
        viewModelScope.launch {
            _state.value = CarOwnerHomeState.Loading
            val result = repository.getHome(token())
            _state.value = if (result.isSuccess) {
                CarOwnerHomeState.Success(result.getOrThrow())
            } else {
                CarOwnerHomeState.Error(result.exceptionOrNull()?.message ?: "Failed to load home")
            }
        }
    }

    // ── Scoped to a specific vehicle — called from vehicle detail screen ──────
    fun loadHomeForVehicle(vehicleId: Long) {
        viewModelScope.launch {
            _state.value = CarOwnerHomeState.Loading
            val result = repository.getHomeByVehicle(token(), vehicleId)
            _state.value = if (result.isSuccess) {
                CarOwnerHomeState.Success(result.getOrThrow())
            } else {
                CarOwnerHomeState.Error(result.exceptionOrNull()?.message ?: "Failed to load home")
            }
        }
    }
}