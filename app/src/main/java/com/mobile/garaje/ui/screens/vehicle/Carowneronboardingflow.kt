package com.mobile.garaje.ui.screens.carowner.onboarding

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.CarOwnerOnboardingState
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.viewmodel.CarOwnerOnboardingViewModel
import com.mobile.garaje.ui.viewmodel.CarOwnerSubmitState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CarOwnerOnboardingFlow(
    onOnboardingComplete: () -> Unit = {},
    viewModel: CarOwnerOnboardingViewModel = viewModel()
) {
    val context        = LocalContext.current
    val submitState    by viewModel.submitState.collectAsStateWithLifecycle()
    val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()

    var currentStep     by remember { mutableStateOf(1) }
    var onboardingState by remember { mutableStateOf(CarOwnerOnboardingState()) }

    LaunchedEffect(submitState) {
        when (submitState) {
            is CarOwnerSubmitState.Success -> {
                Toast.makeText(
                    context,
                    (submitState as CarOwnerSubmitState.Success).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetSubmitState()
                onOnboardingComplete()
            }
            is CarOwnerSubmitState.Error -> {
                Toast.makeText(
                    context,
                    (submitState as CarOwnerSubmitState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetSubmitState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

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
            label = "carOwnerOnboardingStep"
        ) { step ->
            when (step) {
                1 -> CarOwnerOnboardingStep1(
                    onNext = { details ->
                        onboardingState = onboardingState.copy(details = details)
                        currentStep = 2
                    }
                )
                2 -> CarOwnerOnboardingStep2(
                    isLoading = submitState is CarOwnerSubmitState.Loading,
                    onBack = { currentStep = 1 },
                    onSubmit = { profilePicUri ->
                        viewModel.createCarOwner(
                            context       = context,
                            details       = onboardingState.details!!,
                            profilePicUri = profilePicUri
                        )
                    }
                )
            }
        }

        // Upload progress overlay — same as Garage and Mechanic flows
        uploadProgress?.let { progress ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = GarageOrange)
                    Text(
                        text = "Setting up your profile… $progress%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
        }
    }
}