package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.GarageOfferingResponse
import com.mobile.garaje.data.model.ServiceDetailResponse
import com.mobile.garaje.data.repository.CarOwnerServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── Sort options for the garage list ──────────────────────────────────────────

enum class GarageSortOption { DISTANCE, PRICE, RATING }

// ── UI state ──────────────────────────────────────────────────────────────────

sealed class ServiceDetailState {
    object Loading                                            : ServiceDetailState()
    data class Error(val message: String)                     : ServiceDetailState()
    data class Success(
        val service: ServiceDetailResponse,
        val garages: List<GarageOfferingResponse>,
        val sortOption: GarageSortOption
    ) : ServiceDetailState()
}

class CarOwnerServiceDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CarOwnerServiceRepository()
    private val prefs      = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun token()    = prefs.getString("token", "") ?: ""

    private val _state = MutableStateFlow<ServiceDetailState>(ServiceDetailState.Loading)
    val state: StateFlow<ServiceDetailState> = _state

    private var currentServiceId: Long = -1L

    fun load(serviceId: Long, latitude: Double, longitude: Double) {
        currentServiceId = serviceId
        viewModelScope.launch {
            _state.value = ServiceDetailState.Loading

            val serviceResult = repository.getServiceDetail(token(), serviceId)
            val garagesResult = repository.getGaragesOfferingService(token(), serviceId, latitude, longitude)

            if (serviceResult.isFailure) {
                _state.value = ServiceDetailState.Error(
                    serviceResult.exceptionOrNull()?.message ?: "Failed to load service"
                )
                return@launch
            }

            val garages = garagesResult.getOrNull() ?: emptyList()
            _state.value = ServiceDetailState.Success(
                service    = serviceResult.getOrThrow(),
                garages    = sortGarages(garages, GarageSortOption.DISTANCE),
                sortOption = GarageSortOption.DISTANCE
            )
        }
    }

    fun changeSortOption(option: GarageSortOption) {
        val current = _state.value
        if (current is ServiceDetailState.Success) {
            _state.value = current.copy(
                garages    = sortGarages(current.garages, option),
                sortOption = option
            )
        }
    }

    private fun sortGarages(
        garages: List<GarageOfferingResponse>,
        option: GarageSortOption
    ): List<GarageOfferingResponse> {
        return when (option) {
            GarageSortOption.DISTANCE -> garages.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
            GarageSortOption.PRICE    -> garages.sortedBy { it.price ?: Double.MAX_VALUE }
            GarageSortOption.RATING   -> garages.sortedByDescending { it.rating ?: 0.0 }
        }
    }
}