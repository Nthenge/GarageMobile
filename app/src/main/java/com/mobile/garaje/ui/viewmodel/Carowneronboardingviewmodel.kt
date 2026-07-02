package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.CarOwnerDetailsData
import com.mobile.garaje.data.repository.CarOwnerOnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CarOwnerSubmitState {
    object Idle    : CarOwnerSubmitState()
    object Loading : CarOwnerSubmitState()
    data class Success(val message: String) : CarOwnerSubmitState()
    data class Error(val message: String)   : CarOwnerSubmitState()
}

class CarOwnerOnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CarOwnerOnboardingRepository()
    private val prefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun getToken(): String = prefs.getString("token", "") ?: ""

    private val _submitState = MutableStateFlow<CarOwnerSubmitState>(CarOwnerSubmitState.Idle)
    val submitState: StateFlow<CarOwnerSubmitState> = _submitState

    private val _uploadProgress = MutableStateFlow<Int?>(null)
    val uploadProgress: StateFlow<Int?> = _uploadProgress

    fun createCarOwner(
        context: Context,
        details: CarOwnerDetailsData,
        profilePicUri: Uri?
    ) {
        viewModelScope.launch {
            _submitState.value = CarOwnerSubmitState.Loading
            _uploadProgress.value = 0

            val result = repository.createCarOwner(
                context       = context,
                token         = getToken(),
                details       = details,
                profilePicUri = profilePicUri,
                onProgress    = { _uploadProgress.value = it }
            )

            _uploadProgress.value = null

            _submitState.value = if (result.isSuccess) {
                CarOwnerSubmitState.Success(
                    result.getOrNull()?.message ?: "Profile created successfully!"
                )
            } else {
                CarOwnerSubmitState.Error(
                    result.exceptionOrNull()?.message ?: "Something went wrong"
                )
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = CarOwnerSubmitState.Idle
    }
}