package com.mobile.garaje.ui.screens.onboarding

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.ui.viewmodel.GarageOnboardingViewModel
import com.mobile.garaje.ui.viewmodel.OnboardingSubmitState
import com.mobile.garaje.ui.viewmodel.ServicesState

data class GarageOnboardingState(
    val businessDetails: BusinessDetailsData? = null,
    val compliance: ComplianceData? = null,
    val payment: PaymentData? = null
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GarageOnboardingFlow(
    onOnboardingComplete: () -> Unit = {},
    viewModel: GarageOnboardingViewModel = viewModel()
) {
    val context       = LocalContext.current
    val servicesState by viewModel.servicesState.collectAsStateWithLifecycle()
    val submitState   by viewModel.submitState.collectAsStateWithLifecycle()

    var currentStep     by remember { mutableStateOf(1) }
    var onboardingState by remember { mutableStateOf(GarageOnboardingState()) }

    LaunchedEffect(submitState) {
        when (submitState) {
            is OnboardingSubmitState.Success -> {
                Toast.makeText(
                    context,
                    (submitState as OnboardingSubmitState.Success).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetSubmitState()
                onOnboardingComplete()
            }
            is OnboardingSubmitState.Error -> {
                Toast.makeText(
                    context,
                    (submitState as OnboardingSubmitState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetSubmitState()
            }
            else -> {}
        }
    }

    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) togetherWith
                        slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300))
            } else {
                slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) togetherWith
                        slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
            }
        },
        label = "onboardingStep"
    ) { step ->
        when (step) {
            1 -> GarageOnboardingStep1(
                onNext = { details ->
                    onboardingState = onboardingState.copy(businessDetails = details)
                    currentStep = 2
                }
            )
            2 -> GarageOnboardingStep2(
                availableServices = when (servicesState) {
                    is ServicesState.Success -> (servicesState as ServicesState.Success).items
                    else -> emptyList()
                },
                isLoadingServices = servicesState is ServicesState.Loading,
                onBack = { currentStep = 1 },
                onNext = { compliance ->
                    onboardingState = onboardingState.copy(compliance = compliance)
                    currentStep = 3
                }
            )
            3 -> GarageOnboardingStep3(
                isLoading = submitState is OnboardingSubmitState.Loading,
                onBack = { currentStep = 2 },
                onSubmit = { payment ->
                    val finalState = onboardingState.copy(payment = payment)
                    onboardingState = finalState
                    viewModel.submitOnboarding(context, finalState)
                }
            )
        }
    }
}