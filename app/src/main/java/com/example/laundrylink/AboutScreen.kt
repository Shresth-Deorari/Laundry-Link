package com.example.laundrylink

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.padding(10.dp))
            Text(
                text = "About LaundryLink:",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = "This app helps users manage their laundry by allowing them to book machines, " +
                        "track booking counts, and stay updated on machine availability.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Text(
                text = "Developer Information:",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = "Developed by Shresth Deorari, an aspiring Android developer passionate about creating " +
                        "efficient, user-friendly applications that solve everyday problems.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Text(
                text = "Contact Information:",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = "For support or feedback, please contact me at shresthdeorari38@gmail.com.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }

        Text(
            text = "Version 1.0 (Initial Release)",
            fontSize = 14.sp,
            color = Color.Gray,
        )
    }
}
