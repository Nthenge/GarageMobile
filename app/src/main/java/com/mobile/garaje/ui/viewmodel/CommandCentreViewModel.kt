package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.repository.CommandCentreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CommandCentreState {
    object Idle    : CommandCentreState()
    object Loading : CommandCentreState()
    data class Success(val data: CommandCentreResponse) : CommandCentreState()
    data class Error(val message: String)               : CommandCentreState()
}

class CommandCentreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CommandCentreRepository()
    private val prefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow<CommandCentreState>(CommandCentreState.Idle)
    val state: StateFlow<CommandCentreState> = _state

    private val _nearbyMechanics = MutableStateFlow<List<NearbyMechanicResponse>>(emptyList())
    val nearbyMechanics: StateFlow<List<NearbyMechanicResponse>> = _nearbyMechanics

    private val _todayBookings = MutableStateFlow<List<TodayBookingResponse>>(emptyList())
    val todayBookings: StateFlow<List<TodayBookingResponse>> = _todayBookings

    private fun getToken(): String = prefs.getString("token", "") ?: ""
    private fun getGarageId(): Long = prefs.getLong("garageId", -1L)

    fun loadCommandCentre() {
        val token    = getToken()
        val garageId = getGarageId()

        if (token.isEmpty() || garageId == -1L) {
            _state.value = CommandCentreState.Error("Session expired. Please log in again.")
            return
        }

        viewModelScope.launch {
            _state.value = CommandCentreState.Loading

            val result = repository.getCommandCentre(token, garageId)

            _state.value = if (result.isSuccess) {
                CommandCentreState.Success(result.getOrNull()!!)
            } else {
                CommandCentreState.Error(result.exceptionOrNull()?.message ?: "Failed to load")
            }
        }
    }

    fun refreshNearbyMechanics() {
        val token    = getToken()
        val garageId = getGarageId()
        if (token.isEmpty() || garageId == -1L) return

        viewModelScope.launch {
            val result = repository.getNearbyMechanics(token, garageId)
            if (result.isSuccess) {
                _nearbyMechanics.value = result.getOrNull() ?: emptyList()
            }
        }
    }


    fun refreshTodayBookings() {
        val token    = getToken()
        val garageId = getGarageId()
        if (token.isEmpty() || garageId == -1L) return

        viewModelScope.launch {
            val result = repository.getTodayBookings(token, garageId)
            if (result.isSuccess) {
                _todayBookings.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    fun resetState() {
        _state.value = CommandCentreState.Idle
    }
}