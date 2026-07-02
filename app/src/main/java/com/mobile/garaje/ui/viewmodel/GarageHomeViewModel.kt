package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.GarageHomeResponse
import com.mobile.garaje.data.repository.GarageHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GarageHomeState {
    object Loading                                   : GarageHomeState()
    data class Success(val data: GarageHomeResponse) : GarageHomeState()
    data class Error(val message: String)            : GarageHomeState()
}

class GarageHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GarageHomeRepository()
    private val prefs      = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun getToken() = prefs.getString("token", "") ?: ""

    private val _state = MutableStateFlow<GarageHomeState>(GarageHomeState.Loading)
    val state: StateFlow<GarageHomeState> = _state

    init { loadHome() }

    fun loadHome() {
        viewModelScope.launch {
            _state.value = GarageHomeState.Loading
            val result = repository.getGarageHome(getToken())
            _state.value = if (result.isSuccess) {
                GarageHomeState.Success(result.getOrNull()!!)
            } else {
                // TODO: remove fallback once /garage/home is live
                GarageHomeState.Success(repository.getPlaceholderHome())
            }
        }
    }
}