package com.example.laundrylink

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen(historyRepository: HistoryRepository) {
    var historyList by remember { mutableStateOf(emptyList<HistoryRecord>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        historyList = historyRepository.getHistory(20, 0)
        isLoading = false
    }

    if (isLoading) {
        Text("Loading...")
    } else if (historyList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "It's Empty Here",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "You have no booking history available.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.padding(16.dp)
        ) {
            items(historyList) { historyItem ->
                HistoryCard(historyItem)
            }
        }
    }
}


@Composable
fun HistoryCard(historyItem: HistoryRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = "Booking ID: ${historyItem.id}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Machine: ${historyItem.machineNumber}",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = "Date: ${historyItem.date}",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = "In Time: ${convertEpochToString(historyItem.inTime)}",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = "Out Time: ${convertEpochToString(historyItem.outTime)}",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
