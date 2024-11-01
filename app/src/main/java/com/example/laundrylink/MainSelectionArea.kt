package com.example.laundrylink


import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


@Composable
fun MainSelectionArea(sharedPreferences: SharedPreferences, db: FirebaseFirestore) {

    val machineCollection = db.collection("machineStatus")

    val (listOfMachines, setListOfMachines) = remember { mutableStateOf(listOf<MachineInfo>()) }

    getAllMachineStatuses(machineCollection) { machineList ->
        setListOfMachines(machineList)
    }

    // Other state variables
    val bookedState = remember { mutableLongStateOf(sharedPreferences.getLong("BookingCount", 0)) }

    fun refreshMachineStatuses() {
        getAllMachineStatuses(machineCollection) { machineList ->
            setListOfMachines(machineList)
        }
        bookedState.longValue = 0;
    }

    // UI Components
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp)) {
        Text(
            text = "Hi ${sharedPreferences.getString("user_name", "")}!",
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )

        Text(
            text = if (bookedState.longValue != 20L) "You have ${20L - bookedState.longValue} bookings left" else "No more bookings left",
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )

        BookingProgressBar(bookedState, Modifier.padding(start = 13.dp, end = 14.dp, top = 10.dp))

        Spacer(Modifier.padding(vertical = 15.dp))

        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp

        // Define the minimum size for a card item
        val minCardSizeDp = 180.dp

        // Calculate the number of columns by dividing the screen width by the card size
        val columns = (screenWidthDp / minCardSizeDp.value).toInt().coerceAtLeast(1) // Ensure at least 1 column

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
        ) {
            items(listOfMachines.size) { number ->
                val machine = listOfMachines[number]
                MachineGrid(
                    machineNumber = machine.machineNumber,
                    machineStatus = machine.machineStatus,
                    sharedPreferences = sharedPreferences,
                    bookedState = bookedState,
                    machineCollection,
                    listOfMachines
                )
            }
        }
        Button(onClick = {
            machineCollection
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            val documentId = document.id
                            val bookingUpdate = hashMapOf(
                                "bookedBy" to "",
                                "machineStatus" to true
                            )
                            machineCollection.document(documentId)
                                .update(bookingUpdate as Map<String, Any>)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Cleared Machine data for document $documentId")
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Firestore", "Error updating machine: ", exception)
                                }
                        }
                        refreshMachineStatuses() // Refresh the UI after clearing
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error querying machines: ", exception)
                }
        })
        {
            Text(
                text = "Clear all bookings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            )

        }
    }
}

@Composable
fun MachineGrid(
    machineNumber: Long,
    machineStatus: Boolean,
    sharedPreferences: SharedPreferences,
    bookedState: MutableState<Long>,
    machineCollection: CollectionReference,
    listOfMachines: List<MachineInfo>
) {
    val showQRDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                if (machineStatus && bookedState.value < 20) {
                    val bookedMachine = listOfMachines.find { it.machineNumber == machineNumber } ?: return@clickable
                    bookMachine(
                        machineNumber,
                        sharedPreferences.getString("user_name", "") ?: "",
                        machineCollection,
                        bookedMachine
                    )
                    bookedState.value += 1  // Increment booking count locally
                    showQRDialog.value = true
                }
            },
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color(0XFFE8DEF8))
    ) {
        Box(Modifier.aspectRatio(1f)) {
            Text(
                text = "Machine $machineNumber",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                color = Color.Black
            )

            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 10.dp, bottom = 10.dp)
            ) {
                if (machineStatus) {
                    Text(
                        "This machine is free", color = Color.Green,
                        fontSize = 16.sp
                    )
                } else if (bookedState.value == machineNumber) {
                    Text(
                        "XX:XX Time remaining", color = Color.Blue,
                        fontSize = 16.sp
                    )
                } else {
                    Row {
                        Text(
                            "Notify me", color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            // Notification logic
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.notification),
                                contentDescription = "Notify",
                                Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .background(
                        color = if (machineStatus) {
                            Color.Green
                        } else {
                            Color.Red
                        },
                        shape = CircleShape
                    )
            )
        }
    }

    if (showQRDialog.value) {
        QRCodeDialog(onDismiss = { showQRDialog.value = false })
    }
}

@Composable
fun QRCodeDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Scan QR to validate booking", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("QR Code Placeholder")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun BookingProgressBar(booked: MutableLongState, modifier: Modifier) {
    val progress = (booked.longValue / 20.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0XFFE8DEF8))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.Green)
        )
    }
}

fun getAllMachineStatuses(machineCollection: CollectionReference, callback: (List<MachineInfo>) -> Unit) {
    val machineInfoList = mutableListOf<MachineInfo>()

    machineCollection.get()
        .addOnSuccessListener { result ->
            for (document in result) {
                val machineNumber = document.getLong("machineNumber")
                val machineStatus = document.getBoolean("machineStatus")
                val bookedBy = document.getString("bookedBy")
                val expireTimestamp = document.getTimestamp("expireTimestamp")
                val timestamp = document.getTimestamp("timestamp")

                if (machineNumber != null && machineStatus != null) {
                    // Add machine info to the list
                    machineInfoList.add(
                        MachineInfo(machineNumber, machineStatus, bookedBy, expireTimestamp, timestamp)
                    )
                }
            }
            val sortedMachineList = machineInfoList.sortedBy { it.machineNumber }
            // Return the list via callback
            callback(sortedMachineList)
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
}

fun bookMachine(
    machineNumber: Long,
    userId: String,
    machineCollection: CollectionReference,
    bookedMachine: MachineInfo
) {
    // Query Firestore for the document that matches the given machineNumber
    machineCollection
        .whereEqualTo("machineNumber", machineNumber)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Assuming each machineNumber is unique, get the first document
                val document = querySnapshot.documents[0]
                val documentId = document.id  // Get the document ID

                val currentTimestamp = Timestamp.now()
                val calendar = Calendar.getInstance()
                calendar.time = currentTimestamp.toDate()
                calendar.add(Calendar.HOUR_OF_DAY, 2)

                val bookingUpdate = hashMapOf(
                    "bookedBy" to userId,
                    "machineStatus" to false,  // Machine is now booked
                    "expireTimestamp" to Timestamp(calendar.time),
                    "timestamp" to Timestamp.now()
                )

                machineCollection.document(documentId)
                    .update(bookingUpdate as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Machine $machineNumber successfully booked by $userId")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error booking machine: ", exception)
                    }

                bookedMachine.expireTimestamp = Timestamp(calendar.time)
                bookedMachine.machineStatus = false
                bookedMachine.bookedBy = userId
                bookedMachine.timestamp = Timestamp.now()

            } else {
                Log.e("Firestore", "No machine found with machineNumber: $machineNumber")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Error querying machineNumber: ", exception)
        }

}


data class MachineInfo(
    val machineNumber : Long,
    var machineStatus : Boolean,
    var bookedBy : String?,
    var timestamp : Timestamp?,
    var expireTimestamp : Timestamp?
)
