package com.mobile.garaje

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobile.garaje.ui.screens.*
import com.mobile.garaje.ui.screens.vehicle.CarOwnerHomeScreen
import com.mobile.garaje.ui.screens.vehicle.CarOwnerSettingsScreen               // ← ADD 1
import com.mobile.garaje.ui.screens.garage.GarageAdminHomeScreen
import com.mobile.garaje.ui.screens.mechanic.MechanicHomeScreen
import com.mobile.garaje.ui.screens.mechanic.MechanicOnboardingFlow
import com.mobile.garaje.ui.screens.carowner.onboarding.CarOwnerOnboardingFlow
import com.mobile.garaje.ui.screens.garage.CommandCentreScreen
import com.mobile.garaje.ui.screens.garage.GarageBookingsScreen
import com.mobile.garaje.ui.screens.garage.GarageTeamScreen
import com.mobile.garaje.ui.screens.home.ForgotPasswordScreen
import com.mobile.garaje.ui.screens.home.LoginScreen
import com.mobile.garaje.ui.screens.home.RegisterScreen
import com.mobile.garaje.ui.screens.home.SplashScreen
import com.mobile.garaje.ui.screens.mechanic.AddMechanicScreen
import com.mobile.garaje.ui.screens.onboarding.GarageOnboardingFlow
import com.mobile.garaje.ui.screens.support.ChatConversationsScreen
import com.mobile.garaje.ui.screens.utils.SupportScreen
import com.mobile.garaje.ui.screens.utils.ReportIncidentScreen
import com.mobile.garaje.ui.screens.utils.FaqScreen
import com.mobile.garaje.ui.screens.utils.ChatScreen
import com.mobile.garaje.ui.viewmodel.AuthState
import com.mobile.garaje.ui.viewmodel.AuthViewModel
import com.mobile.garaje.ui.viewmodel.GarageOnboardingViewModel
import com.mobile.garaje.ui.viewmodel.GarageOnboardingViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GarageApp() }
    }
}

@Composable
fun GarageApp() {
    val navController     = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState         by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            scope.launch {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            authViewModel.resetState()
        }
    }

    Scaffold(
        modifier     = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) @Suppress("UNUSED_PARAMETER") { _ ->
        NavHost(
            navController    = navController,
            startDestination = "splash",
            modifier         = Modifier.fillMaxSize()
        ) {

            composable("splash") {
                SplashScreen(
                    onSplashFinished = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } },
                    onSkipClick      = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } },
                    onLoginClick     = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } },
                    onRegisterClick  = { navController.navigate("register") { popUpTo("splash") { inclusive = true } } }
                )
            }

            composable("login") {
                LaunchedEffect(authState) {
                    if (authState is AuthState.Success) {
                        val role             = authViewModel.getRole()
                        val detailsCompleted = authViewModel.isDetailsCompleted()
                        val destination = when {
                            role == "GARAGE_ADMIN" && !detailsCompleted -> "garage_onboarding"
                            role == "GARAGE_ADMIN" && detailsCompleted  -> "garage_home"
                            role == "MECHANIC"     && !detailsCompleted -> "mechanic_onboarding"
                            role == "MECHANIC"     && detailsCompleted  -> "mechanic_home"
                            role == "CAR_OWNER"    && !detailsCompleted -> "car_owner_onboarding"
                            role == "CAR_OWNER"    && detailsCompleted  -> "car_owner_home"
                            else                                         -> "car_owner_home"
                        }
                        navController.navigate(destination) { popUpTo("login") { inclusive = true } }
                        authViewModel.resetState()
                    }
                }
                LoginScreen(
                    isLoading             = authState is AuthState.Loading,
                    onLoginClick          = { email, password -> authViewModel.login(email, password) },
                    onRegisterClick       = { navController.navigate("register") },
                    onForgotPasswordClick = { navController.navigate("forgot") }
                )
            }

            composable("register") {
                LaunchedEffect(authState) {
                    if (authState is AuthState.Success) {
                        scope.launch { snackbarHostState.showSnackbar((authState as AuthState.Success).message) }
                        navController.navigate("login") { popUpTo("register") { inclusive = true } }
                        authViewModel.resetState()
                    }
                }
                RegisterScreen(
                    isLoading       = authState is AuthState.Loading,
                    onCreateAccount = { firstName, lastName, email, phone, password, role ->
                        authViewModel.register(
                            email       = email,
                            firstname   = firstName,
                            secondname  = lastName,
                            password    = password,
                            phoneNumber = phone,
                            role        = role.name
                        )
                    },
                    onSignInClick = {
                        navController.navigate("login") { popUpTo("register") { inclusive = true } }
                    }
                )
            }

            composable("forgot") {
                LaunchedEffect(authState) {
                    if (authState is AuthState.Success) {
                        scope.launch { snackbarHostState.showSnackbar((authState as AuthState.Success).message) }
                        authViewModel.resetState()
                    }
                }
                ForgotPasswordScreen(
                    isLoading   = authState is AuthState.Loading,
                    onBackClick = { navController.popBackStack() },
                    onNextClick = { email -> authViewModel.forgotPassword(email) }
                )
            }

            composable("garage_onboarding") {
                val token = authViewModel.getToken()
                val garageViewModel: GarageOnboardingViewModel = viewModel(
                    factory = GarageOnboardingViewModelFactory(token)
                )
                GarageOnboardingFlow(
                    viewModel            = garageViewModel,
                    onOnboardingComplete = {
                        navController.navigate("garage_home") { popUpTo("garage_onboarding") { inclusive = true } }
                    }
                )
            }

            composable("mechanic_onboarding") {
                MechanicOnboardingFlow(
                    onOnboardingComplete = {
                        navController.navigate("mechanic_home") { popUpTo("mechanic_onboarding") { inclusive = true } }
                    }
                )
            }

            composable("car_owner_onboarding") {
                CarOwnerOnboardingFlow(
                    onOnboardingComplete = {
                        navController.navigate("car_owner_home") { popUpTo("car_owner_onboarding") { inclusive = true } }
                    }
                )
            }

            composable("garage_home") {
                GarageAdminHomeScreen(
                    onNavigateToCommandCentre = { navController.navigate("command_centre") },
                    onNavigateToServices      = { /* TODO */ },
                    onNavigateToBookings      = { navController.navigate("garage_bookings") },
                    onNavigateToTeam          = { navController.navigate("garage_team") },
                    onNavigateToAddMechanic   = { navController.navigate("register_mechanic") },
                    onNavigateToRevenue       = { /* TODO */ },
                    onPendingActionClick      = { navController.navigate("garage_bookings") },
                    onAlertClick              = { /* TODO */ },
                    onLogout                  = {
                        authViewModel.clearSession()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable("mechanic_home") {
                MechanicHomeScreen(
                    onNavigateToJobFeed  = { /* TODO */ },
                    onNavigateToMyJobs   = { /* TODO */ },
                    onNavigateToEarnings = { /* TODO */ },
                    onLogout             = {
                        authViewModel.clearSession()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable("car_owner_home") {
                CarOwnerHomeScreen(
                    onNavigateToBookService   = { /* TODO */ },
                    onNavigateToBookings      = { /* TODO */ },
                    onNavigateToMyCars        = { /* TODO */ },
                    onNavigateToGaragesNearby = { /* TODO */ },
                    onNavigateToActiveService = { /* TODO */ },
                    onNavigateToSupport       = { navController.navigate("support") },
                    onNavigateToSettings      = { navController.navigate("car_owner_settings") }
                )
            }

            // ── Car Owner Settings ────────────────────────────────────────────  ← ADD 2
            composable("car_owner_settings") {
                CarOwnerSettingsScreen(
                    firstName  = authViewModel.getFirstName(),
                    initials   = authViewModel.getInitials(),
                    onHome     = {
                        navController.navigate("car_owner_home") {
                            popUpTo("car_owner_home") { inclusive = true }
                        }
                    },
                    onServices = { /* TODO */ },
                    onSupport  = { navController.navigate("support") },
                    onLogout   = {
                        authViewModel.clearSession()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable("garage_team") {
                GarageTeamScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAddMechanic  = { navController.navigate("register_mechanic") }
                )
            }

            composable("register_mechanic") {
                AddMechanicScreen(
                    onBack    = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable("command_centre") {
                CommandCentreScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("garage_bookings") {
                GarageBookingsScreen(
                    onNavigateBack   = { navController.popBackStack() },
                    onBookingDetails = { /* TODO */ }
                )
            }

            // ── Support ──────────────────────────────────────────────────────
            composable("support") {
                SupportScreen(
                    onReportIncident = { navController.navigate("report_incident") },
                    onChatWithUs     = { navController.navigate("conversations") },
                    onFaqs           = { navController.navigate("faqs") },
                    onViewAllReports = { navController.navigate("report_incident") },
                    onHome           = { navController.navigate("car_owner_home") { popUpTo("car_owner_home") { inclusive = true } } },
                    onServices       = { /* TODO */ },
                    onSettings       = { navController.navigate("car_owner_settings") }
                )
            }

            composable("report_incident") {
                ReportIncidentScreen(
                    onBack    = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable("faqs") {
                FaqScreen(onBack = { navController.popBackStack() })
            }

            composable("conversations") {
                ChatConversationsScreen(
                    onBack           = { navController.popBackStack() },
                    onOpenChat       = { issueId: Long, isReadOnly: Boolean ->
                        navController.navigate("chat/$issueId")
                    },
                    onReportIncident = { navController.navigate("report_incident") }
                )
            }

            composable(
                "chat/{issueId}",
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) { backStackEntry ->
                val issueId = backStackEntry.arguments?.getLong("issueId") ?: return@composable
                ChatScreen(
                    issueId = issueId,
                    onBack  = { navController.popBackStack() }
                )
            }
        }
    }
}