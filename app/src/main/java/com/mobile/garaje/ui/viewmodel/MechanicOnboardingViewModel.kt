package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.MechanicDocumentsData
import com.mobile.garaje.data.model.MechanicOnboardingState
import com.mobile.garaje.data.model.MechanicPersonalData
import com.mobile.garaje.data.model.MechanicSkillsData
import com.mobile.garaje.data.repository.MechanicOnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MechanicSubmitState {
    object Idle    : MechanicSubmitState()
    object Loading : MechanicSubmitState()
    data class Success(val message: String) : MechanicSubmitState()
    data class Error(val message: String)   : MechanicSubmitState()
}

class MechanicOnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MechanicOnboardingRepository()
    private val prefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun getToken(): String = prefs.getString("token", "") ?: ""

    private val _submitState = MutableStateFlow<MechanicSubmitState>(MechanicSubmitState.Idle)
    val submitState: StateFlow<MechanicSubmitState> = _submitState

    // Upload progress 0–100, shown as overlay (mirrors GarageOnboardingViewModel)
    private val _uploadProgress = MutableStateFlow<Int?>(null)
    val uploadProgress: StateFlow<Int?> = _uploadProgress

    fun submitProfile(
        context: Context,
        personal: MechanicPersonalData,
        skills: MechanicSkillsData,
        documents: MechanicDocumentsData
    ) {
        viewModelScope.launch {
            _submitState.value = MechanicSubmitState.Loading
            _uploadProgress.value = 0

            val result = repository.updateMechanicProfile(
                context   = context,
                token     = getToken(),
                personal  = personal,
                skills    = skills,
                documents = documents,
                onProgress = { progress ->
                    _uploadProgress.value = progress
                }
            )

            _uploadProgress.value = null

            _submitState.value = if (result.isSuccess) {
                MechanicSubmitState.Success(
                    result.getOrNull()?.message ?: "Profile completed successfully!"
                )
            } else {
                MechanicSubmitState.Error(
                    result.exceptionOrNull()?.message ?: "Something went wrong"
                )
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = MechanicSubmitState.Idle
    }
}