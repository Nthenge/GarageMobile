package com.mobile.garaje.ui.screens.mechanic

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.viewmodel.AddMechanicState
import com.mobile.garaje.ui.viewmodel.AddMechanicViewModel

private val OrangeLight   = Color(0xFFFEF0E6)
private val OrangeDark    = Color(0xFF993C1D)
private val FieldBg       = Color(0xFFF7F7F7)
private val FieldBorder   = Color(0xFFE0E0E0)
private val HintGray      = Color(0xFF9E9E9E)
private val LabelGray     = Color(0xFF6B6B6B)
private val TitleBlack    = Color(0xFF1A1A1A)

@Composable
fun AddMechanicScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AddMechanicViewModel = viewModel()
) {
    val context = LocalContext.current
    val state   by viewModel.state.collectAsStateWithLifecycle()

    var firstname   by remember { mutableStateOf("") }
    var secondname  by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var firstnameError   by remember { mutableStateOf(false) }
    var secondnameError  by remember { mutableStateOf(false) }
    var emailError       by remember { mutableStateOf(false) }
    var phoneError       by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (state) {
            is AddMechanicState.Success -> {
                Toast.makeText(context, (state as AddMechanicState.Success).message,
                    Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onSuccess()
            }
            is AddMechanicState.Error -> {
                Toast.makeText(context, (state as AddMechanicState.Error).message,
                    Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val isLoading = state is AddMechanicState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, FieldBorder, CircleShape)
                    .background(FieldBg)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = TitleBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Add mechanic",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TitleBlack
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Mechanic details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = TitleBlack
                )
                Text(
                    text = "A password will be auto-generated and sent to the mechanic's email.",
                    fontSize = 13.sp,
                    color = LabelGray,
                    lineHeight = 19.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MechanicTextField(
                    modifier = Modifier.weight(1f),
                    label = "First name",
                    value = firstname,
                    onValueChange = { firstname = it; firstnameError = false },
                    placeholder = "e.g. James",
                    icon = Icons.Outlined.Person,
                    isError = firstnameError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
                )
                MechanicTextField(
                    modifier = Modifier.weight(1f),
                    label = "Last name",
                    value = secondname,
                    onValueChange = { secondname = it; secondnameError = false },
                    placeholder = "e.g. Mutua",
                    icon = Icons.Outlined.Person,
                    isError = secondnameError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
                )
            }

            MechanicTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Email address",
                value = email,
                onValueChange = { email = it; emailError = false },
                placeholder = "mechanic@email.com",
                icon = Icons.Outlined.Email,
                isError = emailError,
                errorMessage = "Enter a valid email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            MechanicTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Phone number",
                value = phoneNumber,
                onValueChange = { phoneNumber = it; phoneError = false },
                placeholder = "07XXXXXXXX",
                icon = Icons.Outlined.Phone,
                isError = phoneError,
                errorMessage = "Required",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            InfoCard()

            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    // Validate
                    firstnameError  = firstname.isBlank()
                    secondnameError = secondname.isBlank()
                    emailError      = email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    phoneError      = phoneNumber.isBlank()

                    if (!firstnameError && !secondnameError && !emailError && !phoneError) {
                        viewModel.registerMechanic(
                            firstname   = firstname.trim(),
                            secondname  = secondname.trim(),
                            email       = email.trim(),
                            phoneNumber = phoneNumber.trim()
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GarageOrange,
                    disabledContainerColor = GarageOrange.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Add mechanic",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Text(
                text = "The mechanic will be linked to your garage automatically",
                fontSize = 11.sp,
                color = HintGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun MechanicTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.6.sp,
            color = LabelGray
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = placeholder, fontSize = 14.sp, color = HintGray)
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error else HintGray,
                    modifier = Modifier.size(18.dp)
                )
            },
            isError = isError,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FieldBg,
                focusedContainerColor   = Color.White,
                unfocusedBorderColor    = FieldBorder,
                focusedBorderColor      = GarageOrange,
                errorBorderColor        = MaterialTheme.colorScheme.error,
                cursorColor             = GarageOrange
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(OrangeLight)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = GarageOrange,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "What happens next?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = OrangeDark
            )
            Text(
                text = "The mechanic will receive their login credentials via email. " +
                        "They will then log in and complete their own profile — " +
                        "specialisations, vehicle brands, documents, and availability.",
                fontSize = 12.sp,
                color = OrangeDark,
                lineHeight = 17.sp
            )
        }
    }
}