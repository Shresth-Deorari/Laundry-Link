package com.example.laundrylink

import DrawerContent
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val historyRepository by lazy { HistoryRepository(this) }
        val sharedPreferences = getSharedPreferences("LaundryLink", MODE_PRIVATE)
        val db = Firebase.firestore
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreen(sharedPreferences, historyRepository,db)
        }
    }
}

@Composable
fun HomeScreen(sharedPreferences: SharedPreferences, historyRepository: HistoryRepository, db: FirebaseFirestore) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val showTopBar = remember { mutableStateOf(true) }
    val allowDrawerInteraction = remember { mutableStateOf(true) }
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            showTopBar.value = destination.route != Login.route && destination.route != Signup.route
            allowDrawerInteraction.value = destination.route != Login.route && destination.route != Signup.route
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = allowDrawerInteraction.value,
        drawerContent = {
            DrawerContent(navController, drawerState, sharedPreferences)
        }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar.value) {
                    TopAppBar(drawerState, coroutineScope)
                }
            }
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) Home.route else Login.route
                ) {
                    composable(Home.route) {
                        MainSelectionArea(sharedPreferences, db)
                    }
                    composable(History.route) {
                        HistoryScreen(historyRepository)
                    }
                    composable(About.route) {
                        AboutScreen()
                    }
                    composable(NewFeedback.route) {
                        NewFeedback()
                    }
                    composable(Login.route) {
                        LoginScreen(navController, db, sharedPreferences)
                    }
                    composable(Signup.route) {
                        SignupScreen(navController, db, sharedPreferences)
                    }
                }
            }
        }
    }
}


@Entity
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true) val id : Int =0,
    val machineNumber: Int,
    val date: Long,
    val inTime: Long,
    val outTime: Long
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM HistoryRecord ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getPagedHistory(limit: Int, offset: Int): List<HistoryRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(historyItem: HistoryRecord)

    @Delete
    suspend fun deleteHistory(historyItem: HistoryRecord)

    @Query("DELETE FROM HistoryRecord")
    suspend fun clearHistory()
}

@Database(entities = [HistoryRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao(): HistoryDao
}

class HistoryRepository(context : Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "HistoryRecords"
    ).build()

    suspend fun getHistory(limit: Int, offset: Int) = database.historyDao().getPagedHistory(limit,offset)

    suspend fun addHistory(item: HistoryRecord) = database.historyDao().insertHistory(item)

    suspend fun clearHistory() = database.historyDao().clearHistory()

    suspend fun deleteHistory(item: HistoryRecord) = database.historyDao().deleteHistory(item)
}
