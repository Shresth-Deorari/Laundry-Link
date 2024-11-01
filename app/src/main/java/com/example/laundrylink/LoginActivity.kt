package com.example.laundrylink

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    navController: NavHostController,
    db: FirebaseFirestore,
    sharedPreferences: SharedPreferences
) {

    var webmailId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    val firebaseAuth = FirebaseAuth.getInstance()

    val webmailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Image
        Image(
            painter = painterResource(id = R.drawable.detergent), // Replace with your actual logo resource
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp) // Adjust size as needed
        )
        Spacer(modifier = Modifier.height(16.dp))

        // App Name
        Text(
            text = "LaundryLink",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Login",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Webmail ID Input
        OutlinedTextField(
            value = webmailId,
            onValueChange = {
                webmailId = it.trim()
            },
            label = { Text("Webmail ID") },
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    passwordFocusRequester.requestFocus()
                }
            ),
            modifier = Modifier.focusRequester(webmailFocusRequester)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it.trim()
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Handle login on Done action
                    onClickLogin(webmailId, password, isLoading, errorMessage, firebaseAuth, sharedPreferences, db, navController)
                }
            ),
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onClickLogin(webmailId, password, isLoading, errorMessage, firebaseAuth, sharedPreferences, db, navController)
            }
        ) {
            Text("Login")
        }

        if (isLoading.value) {
            CircularProgressIndicator()
        }

        if (errorMessage.value.isNotEmpty()) {
            Text(text = errorMessage.value, color = Color.Red)
        }

        Spacer(Modifier.padding(top = 5.dp))
        Text(
            text = "New User? Sign up here",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    navController.navigate("Signup")
                }
        )
    }
}

fun onClickLogin(
    webmailId: String,
    password: String,
    isLoading: MutableState<Boolean>,
    errorMessage: MutableState<String>,
    firebaseAuth: FirebaseAuth,
    sharedPreferences: SharedPreferences,
    db: FirebaseFirestore,
    navController: NavHostController
) {
    if (webmailId.isEmpty() || password.isEmpty()) {
        errorMessage.value = "Fields cannot be empty"
    } else {
        isLoading.value = true
        firebaseAuth.signInWithEmailAndPassword(webmailId, password)
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    userId?.let {
                        // Fetch user data from Firestore
                        db.collection("users").document(it).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // User data exists, retrieve it
                                    val name = document.getString("name")
                                    val rollNumber = document.getString("rollNumber")
                                    val webmailId = document.getString("webmailId")

                                    // Save the retrieved data into SharedPreferences
                                    sharedPreferences.edit().apply {
                                        putString("user_name", name)
                                        putString("webmail_id", webmailId)
                                        putString("roll_number", rollNumber)
                                        apply()
                                    }
                                    updateLoginState(sharedPreferences, true)
                                    navController.popBackStack(Login.route, true)
                                    navController.navigate("home")
                                } else {
                                    errorMessage.value = "User data does not exist"
                                }
                            }
                            .addOnFailureListener { e ->
                                errorMessage.value = "Error fetching user data: ${e.message}"
                            }
                    }
                } else {
                    errorMessage.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }
}

private fun updateLoginState(sharedPreferences: SharedPreferences, isLoggedIn: Boolean) {
    sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
}

