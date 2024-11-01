package com.example.laundrylink

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewFeedback(){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ){
        val context = LocalContext.current
        val feedbackText =  remember { mutableStateOf("") }
        var feedbackToSend = remember { mutableStateOf("") }
        val isSubmitting = remember { mutableStateOf(false) }
        val rainbowColors = listOf(
            Color.Red,
            Color(0xFFFFA500),  // Orange
            Color.Yellow,
            Color.Green,
            Color.Blue,
            Color(0xFF4B0082),  // Indigo
            Color(0xFF8B00FF)   // Violet
        )
        val brush = remember {
            Brush.linearGradient(
                colors = rainbowColors
            )
        }

        Text(
            text = "Feedback",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Your Feedback is Always Appreciated!",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(Modifier.padding(20.dp))

        OutlinedTextField(
            value = feedbackText.value,
            onValueChange = {feedbackText.value = it },
            label = { Text("Enter your feedback here") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            textStyle = TextStyle(brush)
        )

        Button(
            onClick = {
                isSubmitting.value = true
                submitFeedback(feedbackText.value) {
                    feedbackText.value = ""
                    Toast.makeText(context, "Feedback Sent Successfully", Toast.LENGTH_SHORT).show()
                }
                isSubmitting.value = false
            },
            modifier = Modifier.align(Alignment.End),
            enabled = (!isSubmitting.value)
        ) {
            Text(if (isSubmitting.value) "Submitting..." else "Submit")
        }

    }
}

fun submitFeedback(value: String, function: () -> Unit) {
    function()
}