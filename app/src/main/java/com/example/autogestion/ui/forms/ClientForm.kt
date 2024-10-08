package com.example.autogestion.ui.forms

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autogestion.ui.Home
import com.example.autogestion.ui.components.NavBar
import com.example.autogestion.data.Client
import com.example.autogestion.data.viewModels.ClientViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import com.example.autogestion.ui.utils.DateUtils.dateFormat
import com.example.autogestion.ui.utils.DateUtils.showDatePicker
import com.example.autogestion.ui.utils.NavigationUtils.navigateToHome
import com.example.autogestion.ui.utils.NavigationUtils.navigateToVehicleForm
import java.util.Calendar


class ClientForm : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Retrieve intent extras if available
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        val birthDate = intent.getStringExtra("birthDate") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val address = intent.getStringExtra("address") ?: ""

        setContent {
            ClientFormApp(
                initFirstName = firstName,
                initLastName = lastName,
                initPhoneNumber = phoneNumber,
                initBirthDate = birthDate,
                initEmail = email,
                initAddress = address
            )

        }
    }

    @Composable
    fun ClientFormApp(
        initFirstName: String,
        initLastName: String,
        initPhoneNumber: String,
        initBirthDate: String,
        initEmail: String,
        initAddress: String,
        clientViewModel: ClientViewModel = viewModel()
    ) {
        val context = LocalContext.current

        // State management for input fields
        var firstName by remember { mutableStateOf(TextFieldValue(initFirstName)) }
        var lastName by remember { mutableStateOf(TextFieldValue(initLastName)) }
        var phoneNumber by remember { mutableStateOf(TextFieldValue(initPhoneNumber)) }
        var birthDate by remember { mutableStateOf(TextFieldValue(initBirthDate)) }
        var email by remember { mutableStateOf(TextFieldValue(initEmail)) }
        var address by remember { mutableStateOf(TextFieldValue(initAddress)) }

        // State management for error handling
        var isFirstNameError by remember { mutableStateOf(false) }
        var isLastNameError by remember { mutableStateOf(false) }
        var isPhoneError by remember { mutableStateOf(false) }
        var isBirthDateError by remember { mutableStateOf(false) }
        var phoneExistsError by remember { mutableStateOf(false) }

        // Instance for managing dates
        val calendar = Calendar.getInstance()

        val coroutineScope = rememberCoroutineScope()

        // Form display and user input handling.
        // Each field is bound to a specific part of the client's data.
        // Validators are set to trigger visual indicators of errors (isError).
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                NavBar(
                    text = "Formulaire Client",
                    onBackClick = {
                        navigateToHome(context)
                    }
                )

                // Client input fields with validation handling
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = firstName.text.isEmpty()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = lastName.text.isEmpty()
                )
                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Numéro de téléphone") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneNumber.text.isEmpty() || phoneExistsError
                )
                if (phoneExistsError) {
                    Text(
                        "Ce numéro de téléphone existe déjà.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Date de naissance (optionnel)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    isError = isBirthDateError,
                    trailingIcon = {
                        IconButton(onClick = {
                            /*val datePickerDialog = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(year, month, dayOfMonth)
                                    birthDate = TextFieldValue(dateFormat.format(calendar.time))
                                    isBirthDateError = false
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                            */
                            showDatePicker(context, calendar) { newDate ->
                                birthDate = TextFieldValue(newDate)
                                isBirthDateError = false
                            }
                        }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    }
                )
                if (isBirthDateError) {
                    Text("Format de date invalide", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Button row for client creation and navigation to Home Page
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            isFirstNameError = firstName.text.isEmpty()
                            isLastNameError = lastName.text.isEmpty()
                            isPhoneError = phoneNumber.text.isEmpty()

                            if (!isFirstNameError && !isLastNameError && !isPhoneError && !isBirthDateError) {
                                coroutineScope.launch {
                                    val clientLiveData = clientViewModel.getClientByPhoneNumber(phoneNumber.text)
                                    clientLiveData.observe(context as ComponentActivity) { existingClient ->
                                        if (existingClient != null) {
                                            phoneExistsError = true
                                        } else {
                                            phoneExistsError = false
                                            val birthDateLong = if (birthDate.text.isNotEmpty()) {
                                                dateFormat.parse(birthDate.text)?.time ?: 0L
                                            } else {
                                                null
                                            }
                                            val newClient = Client(
                                                clientId = 0,
                                                lastName = lastName.text,
                                                firstName = firstName.text,
                                                phone = phoneNumber.text,
                                                birthDate = birthDateLong ?: 0L,
                                                email = email.text,
                                                address = address.text
                                            )

                                            coroutineScope.launch {
                                                clientViewModel.addClient(newClient)
                                                navigateToHome(context)
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = firstName.text.isNotEmpty() && lastName.text.isNotEmpty() && phoneNumber.text.isNotEmpty() && !isBirthDateError
                    ) {
                        Text("Enregistrer le client")
                    }

                    // Button for saving data and navigate to next form
                    Button(
                        onClick = {
                            isFirstNameError = firstName.text.isEmpty()
                            isLastNameError = lastName.text.isEmpty()
                            isPhoneError = phoneNumber.text.isEmpty()

                            if (!isFirstNameError && !isLastNameError && !isPhoneError && !isBirthDateError) {
                                coroutineScope.launch {
                                    val clientLiveData = clientViewModel.getClientByPhoneNumber(phoneNumber.text)
                                    clientLiveData.observe(context as ComponentActivity) { existingClient ->
                                        if (existingClient != null) {
                                            phoneExistsError = true
                                        } else {
                                            phoneExistsError = false

                                            navigateToVehicleForm(context, firstName.text, lastName.text, phoneNumber.text, birthDate.text, address.text, email.text)
                                        }
                                    }
                                }
                            }
                        },
                        enabled = firstName.text.isNotEmpty() && lastName.text.isNotEmpty() && phoneNumber.text.isNotEmpty() && !isBirthDateError
                    ) {
                        Text("Suivant")
                    }
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview2() {
        ClientFormApp("", "", "", "", "", "")
    }


}



