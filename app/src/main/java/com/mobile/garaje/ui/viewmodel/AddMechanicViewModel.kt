package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.repository.MechanicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AddMechanicState {
    object Idle    : AddMechanicState()
    object Loading : AddMechanicState()
    data class Success(val message: String) : AddMechanicState()
    data class Error(val message: String)   : AddMechanicState()
}

class AddMechanicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MechanicRepository()

    // Read the stored JWT exactly like AuthViewModel does
    private val prefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun getToken(): String = prefs.getString("token", "") ?: ""

    private val _state = MutableStateFlow<AddMechanicState>(AddMechanicState.Idle)
    val state: StateFlow<AddMechanicState> = _state

    fun registerMechanic(
        firstname: String,
        secondname: String,
        email: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            _state.value = AddMechanicState.Loading

            val result = repository.registerMechanic(
                token       = getToken(),
                firstname   = firstname,
                secondname  = secondname,
                email       = email,
                phoneNumber = phoneNumber
            )

            _state.value = if (result.isSuccess) {
                AddMechanicState.Success(
                    "Mechanic added! Login credentials have been sent to $email."
                )
            } else {
                AddMechanicState.Error(
                    result.exceptionOrNull()?.message ?: "Something went wrong"
                )
            }
        }
    }

    fun resetState() {
        _state.value = AddMechanicState.Idle
    }
}