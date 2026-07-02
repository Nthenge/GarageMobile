package com.mobile.garaje.ui.screens.mechanic

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
import com.mobile.garaje.data.model.MechanicOnboardingState
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.viewmodel.MechanicOnboardingViewModel
import com.mobile.garaje.ui.viewmodel.MechanicSubmitState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MechanicOnboardingFlow(
    onOnboardingComplete: () -> Unit = {},
    viewModel: MechanicOnboardingViewModel = viewModel()
) {
    val context        = LocalContext.current
    val submitState    by viewModel.submitState.collectAsStateWithLifecycle()
    val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()

    var currentStep     by remember { mutableStateOf(1) }
    var onboardingState by remember { mutableStateOf(MechanicOnboardingState()) }

    // React to success / error — same pattern as GarageOnboardingFlow
    LaunchedEffect(submitState) {
        when (submitState) {
            is MechanicSubmitState.Success -> {
                Toast.makeText(context,
                    (submitState as MechanicSubmitState.Success).message,
                    Toast.LENGTH_LONG).show()
                viewModel.resetSubmitState()
                onOnboardingComplete()
            }
            is MechanicSubmitState.Error -> {
                Toast.makeText(context,
                    (submitState as MechanicSubmitState.Error).message,
                    Toast.LENGTH_LONG).show()
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
            label = "mechanicOnboardingStep"
        ) { step ->
            when (step) {
                1 -> MechanicOnboardingStep1(
                    onNext = { personal ->
                        onboardingState = onboardingState.copy(personal = personal)
                        currentStep = 2
                    }
                )
                2 -> MechanicOnboardingStep2(
                    onBack = { currentStep = 1 },
                    onNext = { skills ->
                        onboardingState = onboardingState.copy(skills = skills)
                        currentStep = 3
                    }
                )
                3 -> MechanicOnboardingStep3(
                    isLoading = submitState is MechanicSubmitState.Loading,
                    onBack = { currentStep = 2 },
                    onSubmit = { documents ->
                        val finalState = onboardingState.copy(documents = documents)
                        onboardingState = finalState
                        viewModel.submitProfile(
                            context = context,
                            personal = finalState.personal!!,
                            skills = finalState.skills!!,
                            documents = documents
                        )
                    }
                )
            }
        }

        // Upload progress overlay — identical to GarageOnboardingFlow
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
                        text = "Uploading files… $progress%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
        }
    }
}