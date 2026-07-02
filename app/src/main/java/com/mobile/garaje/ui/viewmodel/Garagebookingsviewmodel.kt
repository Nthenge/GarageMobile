package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.GarageBooking
import com.mobile.garaje.data.repository.GarageBookingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class BookingFilter { ALL, PENDING, CONFIRMED, COMPLETED, CANCELLED }

sealed class GarageBookingsState {
    object Loading                                       : GarageBookingsState()
    data class Success(val bookings: List<GarageBooking>) : GarageBookingsState()
    data class Error(val message: String)                 : GarageBookingsState()
}

class GarageBookingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GarageBookingsRepository()
    private val prefs      = application.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private fun getToken(): String = prefs.getString("token", "") ?: ""

    private val _allBookings = MutableStateFlow<List<GarageBooking>>(emptyList())

    private val _state = MutableStateFlow<GarageBookingsState>(GarageBookingsState.Loading)
    val state: StateFlow<GarageBookingsState> = _state

    private val _selectedFilter = MutableStateFlow(BookingFilter.ALL)
    val selectedFilter: StateFlow<BookingFilter> = _selectedFilter

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _state.value = GarageBookingsState.Loading

            val result = repository.getBookings(getToken())

            val bookings = if (result.isSuccess) {
                result.getOrNull() ?: emptyList()
            } else {
                // Backend endpoint isn't ready yet — fall back to placeholder data.
                // TODO: remove this fallback once /garage/bookings is live.
                repository.getPlaceholderBookings()
            }

            _allBookings.value = bookings
            applyFilters()
        }
    }

    fun setFilter(filter: BookingFilter) {
        _selectedFilter.value = filter
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun confirmBooking(bookingId: Long) {
        viewModelScope.launch {
            val result = repository.confirmBooking(getToken(), bookingId)
            if (result.isSuccess) {
                // Optimistically update local state, then refresh from server
                loadBookings()
            }
        }
    }

    fun declineBooking(bookingId: Long) {
        viewModelScope.launch {
            val result = repository.declineBooking(getToken(), bookingId)
            if (result.isSuccess) {
                loadBookings()
            }
        }
    }

    private fun applyFilters() {
        val filter = _selectedFilter.value
        val query  = _searchQuery.value.trim().lowercase()

        var filtered = _allBookings.value

        filtered = when (filter) {
            BookingFilter.ALL       -> filtered
            BookingFilter.PENDING   -> filtered.filter { it.status == "PENDING" }
            BookingFilter.CONFIRMED -> filtered.filter { it.status == "CONFIRMED" }
            BookingFilter.COMPLETED -> filtered.filter { it.status == "COMPLETED" }
            BookingFilter.CANCELLED -> filtered.filter { it.status == "CANCELLED" }
        }

        if (query.isNotEmpty()) {
            filtered = filtered.filter { booking ->
                listOfNotNull(
                    booking.serviceName,
                    booking.customerName,
                    booking.vehiclePlate,
                    booking.mechanicName
                ).any { it.lowercase().contains(query) }
            }
        }

        _state.value = GarageBookingsState.Success(filtered)
    }
}