// SignupScreen.kt
package com.example.laundrylink

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun SignupScreen(navController: NavHostController, db: FirebaseFirestore, sharedPreferences: SharedPreferences) {
    var rollNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var webmailId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val rollNumberFocusRequester = FocusRequester()
    val nameFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()

    val firebaseAuth = FirebaseAuth.getInstance()

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

        // Signup Header
        Text(
            text = "Sign Up",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Signup Form Fields
        OutlinedTextField(
            value = webmailId,
            onValueChange = { webmailId = it.trim() },
            label = { Text("Webmail ID") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    rollNumberFocusRequester.requestFocus()
                }
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(FocusRequester())
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = rollNumber,
            onValueChange = { rollNumber = it.trim() },
            label = { Text("Roll Number") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    nameFocusRequester.requestFocus()
                }
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(rollNumberFocusRequester)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it.trim() },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    passwordFocusRequester.requestFocus()
                }
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nameFocusRequester)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.trim() },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Handle signup on Done action
                    performSignup(firebaseAuth, db, navController, webmailId, password, rollNumber, name, errorMessage, sharedPreferences){}
                }
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                performSignup(firebaseAuth, db, navController, webmailId, password, rollNumber, name, errorMessage, sharedPreferences){
                    isLoading = false;
                }
            },
            enabled = !isLoading
        ) {
            Text("Sign Up")
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        if (errorMessage.value.isNotEmpty()) {
            Text(text = errorMessage.value, color = Color.Red)
        }
        Spacer(modifier = Modifier.padding(top = 5.dp))

        Text(
            text = "Existing User? Login Here",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    navController.navigate("Login")
                }
        )
    }
}

private fun performSignup(
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore,
    navController: NavHostController,
    webmailId: String,
    password: String,
    rollNumber: String,
    name: String,
    errorMessage: MutableState<String>,
    sharedPreferences: SharedPreferences,
    onComplete: () -> Unit
) {
    if (webmailId.isBlank() || password.isBlank() || rollNumber.isBlank() || name.isBlank()) {
        errorMessage.value = "All fields must be filled."
        onComplete()
        return
    }

    firebaseAuth.createUserWithEmailAndPassword(webmailId, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid
                userId?.let {
                    val user = hashMapOf(
                        "webmailId" to webmailId,
                        "rollNumber" to rollNumber,
                        "name" to name,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(it).set(user)
                        .addOnSuccessListener {
                            updateLoginState(sharedPreferences, true)
                            navController.popBackStack(Login.route, true)
                            navController.navigate(Home.route)
                        }
                        .addOnFailureListener { e ->
                            errorMessage.value = e.localizedMessage ?: "Signup failed"
                            onComplete() // Set isLoading to false on failure
                        }
                }
            } else {
                errorMessage.value = task.exception?.localizedMessage ?: "Signup failed"
                onComplete()
            }
        }
}

private fun updateLoginState(sharedPreferences: SharedPreferences, isLoggedIn: Boolean) {
    sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
}

