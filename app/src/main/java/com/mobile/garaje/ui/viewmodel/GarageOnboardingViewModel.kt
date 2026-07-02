package com.mobile.garaje.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.repository.GarageRepository
import com.mobile.garaje.ui.screens.onboarding.GarageOnboardingState
import com.mobile.garaje.ui.screens.onboarding.ServiceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OnboardingSubmitState {
    object Idle    : OnboardingSubmitState()
    object Loading : OnboardingSubmitState()
    data class Success(val message: String) : OnboardingSubmitState()
    data class Error(val message: String)   : OnboardingSubmitState()
}

sealed class ServicesState {
    object Loading                                   : ServicesState()
    data class Success(val items: List<ServiceItem>) : ServicesState()
    data class Error(val message: String)            : ServicesState()
}

class GarageOnboardingViewModel(
    private val token: String
) : ViewModel() {

    private val repository = GarageRepository()

    private val _servicesState = MutableStateFlow<ServicesState>(ServicesState.Loading)
    val servicesState: StateFlow<ServicesState> = _servicesState

    private val _submitState = MutableStateFlow<OnboardingSubmitState>(OnboardingSubmitState.Idle)
    val submitState: StateFlow<OnboardingSubmitState> = _submitState

    init { fetchServices() }

    fun fetchServices() {
        viewModelScope.launch {
            _servicesState.value = ServicesState.Loading
            val result = repository.getServices(token)
            _servicesState.value = if (result.isSuccess) {
                val items = result.getOrNull()?.map { svc ->
                    ServiceItem(id = svc.id ?: 0L, name = svc.serviceName.orEmpty())
                } ?: emptyList()
                ServicesState.Success(items)
            } else {
                ServicesState.Error(
                    result.exceptionOrNull()?.message ?: "Could not load services"
                )
            }
        }
    }

    // ── All 3 steps sent as one multipart request ─────────────────────────────

    fun submitOnboarding(context: Context, state: GarageOnboardingState) {
        viewModelScope.launch {
            _submitState.value = OnboardingSubmitState.Loading
            try {
                val d = state.businessDetails
                val c = state.compliance
                val p = state.payment

                val result = repository.createGarage(
                    context                 = context,
                    token                   = token,
                    businessName            = d?.businessName.orEmpty(),
                    businessEmail           = d?.businessEmail.orEmpty(),
                    phoneNumber             = d?.phoneNumber.orEmpty(),
                    physicalAddress         = d?.physicalAddress.orEmpty(),
                    latitude                = d?.latitude,
                    longitude               = d?.longitude,
                    openingTime             = d?.openingTime.orEmpty(),
                    closingTime             = d?.closingTime.orEmpty(),
                    operatingDays           = d?.operatingDays ?: emptyList(),
                    yearsInOperation        = d?.yearsInOperation ?: 0,
                    registrationNumber      = c?.registrationNumber.orEmpty(),
                    licenseNumber           = c?.licenseNumber.orEmpty(),
                    professionalCertificate = c?.professionalCertificate.orEmpty(),
                    serviceIds              = c?.selectedServiceIds ?: emptyList(),
                    paybillNumber           = p?.paybillNumber,
                    accountNumber           = p?.accountNumber,
                    mpesaTill               = p?.mpesaTill,
                    businessLicenseUri      = c?.certificateUri,
                    professionalCertUri     = c?.certificateUri,
                    facilityPhotosUri       = c?.facilityPhotoUris?.firstOrNull()
                )

                _submitState.value = if (result.isSuccess) {
                    OnboardingSubmitState.Success(
                        "Garage profile submitted! We'll review it within 24–48 hours."
                    )
                } else {
                    OnboardingSubmitState.Error(
                        result.exceptionOrNull()?.message ?: "Submission failed"
                    )
                }

            } catch (e: Exception) {
                _submitState.value = OnboardingSubmitState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = OnboardingSubmitState.Idle
    }
}

class GarageOnboardingViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GarageOnboardingViewModel(token) as T
    }
}