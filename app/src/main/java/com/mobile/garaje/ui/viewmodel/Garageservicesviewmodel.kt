package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.model.CategoryResponse
import com.mobile.garaje.data.model.ServiceResponse
import com.mobile.garaje.data.repository.ServicesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI state ──────────────────────────────────────────────────────────────────

data class GarageServicesUiState(
    val isLoading: Boolean                  = true,
    val allServices: List<ServiceResponse>  = emptyList(),
    val myServices: Set<Long>               = emptySet(),   // IDs of services garage offers
    val categories: List<CategoryResponse>  = emptyList(),
    val selectedCategoryId: Long?           = null,         // null = "All"
    val searchQuery: String                 = "",
    val error: String?                      = null,
    val actionMessage: String?              = null          // snackbar feedback
)

class GarageServicesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ServicesRepository()
    private val prefs      = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun token()    = prefs.getString("token", "") ?: ""

    private val _uiState = MutableStateFlow(GarageServicesUiState())
    val uiState: StateFlow<GarageServicesUiState> = _uiState.asStateFlow()

    init { loadAll() }

    // ── Load everything in parallel ───────────────────────────────────────────

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val token = token()

            val allDeferred        = async { repository.getAllServices(token) }
            val myDeferred         = async { repository.getMyGarageServices(token) }
            val categoriesDeferred = async { repository.getAllCategories(token) }

            val allResult        = allDeferred.await()
            val myResult         = myDeferred.await()
            val categoriesResult = categoriesDeferred.await()

            if (allResult.isFailure || myResult.isFailure || categoriesResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = allResult.exceptionOrNull()?.message
                        ?: myResult.exceptionOrNull()?.message
                        ?: categoriesResult.exceptionOrNull()?.message
                )
                return@launch
            }

            val myIds = myResult.getOrNull()
                ?.mapNotNull { it.id }
                ?.toSet() ?: emptySet()

            _uiState.value = _uiState.value.copy(
                isLoading   = false,
                allServices = allResult.getOrNull() ?: emptyList(),
                myServices  = myIds,
                categories  = categoriesResult.getOrNull() ?: emptyList()
            )
        }
    }

    // ── Category tab selection ────────────────────────────────────────────────

    fun selectCategory(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    // ── Search query ──────────────────────────────────────────────────────────

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    // ── Add service ───────────────────────────────────────────────────────────

    fun addService(serviceId: Long) {
        viewModelScope.launch {
            // Optimistic update
            _uiState.value = _uiState.value.copy(
                myServices = _uiState.value.myServices + serviceId
            )
            val result = repository.addService(token(), serviceId)
            if (result.isFailure) {
                // Roll back
                _uiState.value = _uiState.value.copy(
                    myServices     = _uiState.value.myServices - serviceId,
                    actionMessage  = result.exceptionOrNull()?.message ?: "Failed to add service"
                )
            } else {
                // Repository returns the updated service list, not a message —
                // refresh myServices from the authoritative server response
                // and show a static confirmation instead.
                val updatedIds = result.getOrNull()
                    ?.mapNotNull { it.id }
                    ?.toSet()
                _uiState.value = _uiState.value.copy(
                    myServices    = updatedIds ?: _uiState.value.myServices,
                    actionMessage = "Service added"
                )
            }
        }
    }

    // ── Remove service ────────────────────────────────────────────────────────

    fun removeService(serviceId: Long) {
        viewModelScope.launch {
            // Optimistic update
            _uiState.value = _uiState.value.copy(
                myServices = _uiState.value.myServices - serviceId
            )
            val result = repository.removeService(token(), serviceId)
            if (result.isFailure) {
                // Roll back
                _uiState.value = _uiState.value.copy(
                    myServices    = _uiState.value.myServices + serviceId,
                    actionMessage = result.exceptionOrNull()?.message ?: "Failed to remove service"
                )
            } else {
                val updatedIds = result.getOrNull()
                    ?.mapNotNull { it.id }
                    ?.toSet()
                _uiState.value = _uiState.value.copy(
                    myServices    = updatedIds ?: _uiState.value.myServices,
                    actionMessage = "Service removed"
                )
            }
        }
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }

    // ── Derived helpers used by the screen ────────────────────────────────────

    // My services filtered by selected category tab
    fun myServicesFiltered(): List<ServiceResponse> {
        val state = _uiState.value
        return state.allServices.filter { svc ->
            val isOffered = (svc.id ?: -1L) in state.myServices
            val inCategory = state.selectedCategoryId == null
                    || svc.categoryId == state.selectedCategoryId
            isOffered && inCategory
        }
    }

    // Available services filtered by search query and selected category
    // "All" tab shows only first 6 per category as a preview
    fun availableServicesGrouped(): Map<String, List<ServiceResponse>> {
        val state = _uiState.value
        val query = state.searchQuery.trim().lowercase()

        val filtered = state.allServices.filter { svc ->
            val matchesSearch = query.isEmpty()
                    || svc.serviceName?.lowercase()?.contains(query) == true
            val matchesCat = state.selectedCategoryId == null
                    || svc.categoryId == state.selectedCategoryId
            matchesSearch && matchesCat
        }

        val grouped = filtered
            .groupBy { it.categoryName ?: "Other" }
            .toSortedMap()

        // On "All" tab with no search, cap each category at 4 items as preview
        return if (state.selectedCategoryId == null && query.isEmpty()) {
            grouped.mapValues { (_, services) -> services.take(4) }
        } else {
            grouped
        }
    }

    // Categories that have at least one offered service (for the "Your services" tabs)
    fun myServiceCategories(): List<CategoryResponse> {
        val state = _uiState.value
        val myCatIds = state.allServices
            .filter { (it.id ?: -1L) in state.myServices }
            .mapNotNull { it.categoryId }
            .toSet()
        return state.categories.filter { (it.id ?: -1L) in myCatIds }
    }
}