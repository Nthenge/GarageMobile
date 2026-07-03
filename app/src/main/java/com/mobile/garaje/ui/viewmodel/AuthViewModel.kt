package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String)   : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    private val prefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // ── Session helpers ───────────────────────────────────────────────────────

    fun getToken(): String            = prefs.getString("token", "") ?: ""
    fun getRole(): String             = prefs.getString("role", "") ?: ""
    fun isDetailsCompleted(): Boolean = prefs.getBoolean("detailsCompleted", false)
    fun isLoggedIn(): Boolean         = getToken().isNotEmpty()
    fun getFirstName(): String        = prefs.getString("firstname", "") ?: ""     // ← added
    fun getInitials(): String         = prefs.getString("initials", "?") ?: "?"   // ← added

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    // ── Builds "JM" from "James" + "Mutua" ───────────────────────────────────

    private fun buildInitials(firstname: String?, secondname: String?): String {
        val f = firstname?.trim()?.firstOrNull()?.uppercaseChar()
        val s = secondname?.trim()?.firstOrNull()?.uppercaseChar()
        return when {
            f != null && s != null -> "$f$s"
            f != null              -> "$f"
            else                   -> "?"
        }
    }

    // ── Auth calls ────────────────────────────────────────────────────────────

    fun register(
        email: String,
        firstname: String,
        secondname: String,
        password: String,
        phoneNumber: String,
        role: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, firstname, secondname, password, phoneNumber, role)
            _authState.value = if (result.isSuccess) {
                AuthState.Success("Account created! Please check your email to verify.")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            _authState.value = if (result.isSuccess) {
                val response = result.getOrNull()

                prefs.edit()
                    .putString("token",             response?.token                                      ?: "")
                    .putString("role",              response?.role                                       ?: "")
                    .putString("firstname",         response?.firstname                                  ?: "")
                    .putString("initials",          buildInitials(response?.firstname, response?.secondname))
                    .putBoolean("detailsCompleted", response?.detailsCompleted                          ?: false)
                    .putString("userId", response?.userId.toString())
                    .apply()

                AuthState.Success("Login successful")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.forgotPassword(email)
            _authState.value = if (result.isSuccess) {
                AuthState.Success("Reset link sent to $email")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Request failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}