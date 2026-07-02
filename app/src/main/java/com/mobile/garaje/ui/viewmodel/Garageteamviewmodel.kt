package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.GarageMechanicResponse
import com.mobile.garaje.data.repository.MechanicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── Team list state ───────────────────────────────────────────────────────────

sealed class TeamListState {
    object Loading                                          : TeamListState()
    data class Success(val mechanics: List<GarageMechanicResponse>) : TeamListState()
    data class Error(val message: String)                   : TeamListState()
}

// ── Register mechanic state ───────────────────────────────────────────────────

sealed class RegisterMechanicState {
    object Idle    : RegisterMechanicState()
    object Loading : RegisterMechanicState()
    data class Success(val message: String) : RegisterMechanicState()
    data class Error(val message: String)   : RegisterMechanicState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class GarageTeamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MechanicRepository()
    private val prefs      = application.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private fun getToken(): String = prefs.getString("token", "") ?: ""

    // Team list
    private val _teamState = MutableStateFlow<TeamListState>(TeamListState.Loading)
    val teamState: StateFlow<TeamListState> = _teamState

    // Register form
    private val _registerState = MutableStateFlow<RegisterMechanicState>(RegisterMechanicState.Idle)
    val registerState: StateFlow<RegisterMechanicState> = _registerState

    init {
        loadMechanics()
    }

    // ── Load garage mechanics ─────────────────────────────────────────────────

    fun loadMechanics() {
        viewModelScope.launch {
            _teamState.value = TeamListState.Loading
            val result = repository.getGarageMechanics(getToken())
            _teamState.value = if (result.isSuccess) {
                TeamListState.Success(result.getOrNull() ?: emptyList())
            } else {
                TeamListState.Error(result.exceptionOrNull()?.message ?: "Failed to load team")
            }
        }
    }

    // ── Register a new mechanic ───────────────────────────────────────────────

    fun registerMechanic(
        firstname: String,
        secondname: String,
        email: String,
        phoneNumber: String
    ) {
        if (firstname.isBlank() || secondname.isBlank() || email.isBlank() || phoneNumber.isBlank()) {
            _registerState.value = RegisterMechanicState.Error("All fields are required")
            return
        }
        viewModelScope.launch {
            _registerState.value = RegisterMechanicState.Loading
            val result = repository.registerMechanic(
                token       = getToken(),
                firstname   = firstname.trim(),
                secondname  = secondname.trim(),
                email       = email.trim(),
                phoneNumber = phoneNumber.trim()
            )
            _registerState.value = if (result.isSuccess) {
                // Refresh the list after successful registration
                loadMechanics()
                RegisterMechanicState.Success(
                    "Mechanic registered! Login credentials have been sent to $email"
                )
            } else {
                RegisterMechanicState.Error(
                    result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterMechanicState.Idle
    }
}