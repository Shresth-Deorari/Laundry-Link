package com.example.laundrylink

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
@Composable
fun BottomAppBar(navController : NavController){
    val list = listOf<Destinations>(
        Home,
        History
    )

    val selectedIndex =  rememberSaveable{ mutableIntStateOf(0)}

//    BottomNavigation(){
//
//    }
}


