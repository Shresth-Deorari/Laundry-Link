import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.laundrylink.About
import com.example.laundrylink.History
import com.example.laundrylink.Home
import com.example.laundrylink.Login
import com.example.laundrylink.NewFeedback
import com.example.laundrylink.R
import com.example.laundrylink.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, sharedPreferences: SharedPreferences) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val coroutineScope = rememberCoroutineScope()
    val destinations = listOf(Home, History, NewFeedback)
    val userDestination = User
    val aboutDestination = About
    val logOut = Login

    ModalDrawerSheet {
        Spacer(Modifier.padding(top = 20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors  = CardDefaults.cardColors(Color(0XFFE8DEF8)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            val userName = sharedPreferences.getString("user_name", "User Name") ?: "User Name"
            val rollNumber = sharedPreferences.getString("roll_number", "Roll No") ?: "Roll No"
            val webEmail = sharedPreferences.getString("webmail_id", "Email") ?: "Email"
            Row(Modifier.fillMaxWidth()){
                Image(
                    painter = painterResource(id = userDestination.icon),
                    contentDescription = "User Icon",
                    Modifier.padding(16.dp)
                        .size(50.dp)
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Roll No: $rollNumber",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = webEmail,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(Modifier.padding(top = 20.dp))

        destinations.forEach { destination ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = destination.title,
                        color = if (currentRoute == destination.route) Color.Blue else Color.Black
                    )
                },
                selected = currentRoute == destination.route,
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                icon = {
                    Image(
                        painter = painterResource(id = destination.icon),
                        contentDescription = destination.title,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        NavigationDrawerItem(
            label = {
                Text(
                    text = "Logout",
                    color = if (currentRoute == Login.route) Color.Blue else Color.Black
                )
            },
            selected = currentRoute == Login.route,
            onClick = {
                // Firebase sign-out
                FirebaseAuth.getInstance().signOut()

                // Navigate to the Login screen, clearing the back stack
                navController.navigate(Login.route) {
                    popUpTo(0) { // Clear entire back stack
                        inclusive = true // Ensures all previous destinations are cleared
                    }
                    launchSingleTop = true // Prevents duplicate instances of Login screen
                }

                // Close the drawer
                coroutineScope.launch {
                    drawerState.close()
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = aboutDestination.title,
                    modifier = Modifier.size(24.dp).padding(start = 2.dp)
                )
            }
        )



        NavigationDrawerItem(
            label = {
                Text(
                    text = aboutDestination.title,
                    color = if (currentRoute == aboutDestination.route) Color.Blue else Color.Black
                )
            },
            selected = currentRoute == aboutDestination.route,
            onClick = {
                if (currentRoute != aboutDestination.route) {
                    navController.navigate(aboutDestination.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                coroutineScope.launch {
                    drawerState.close()
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = aboutDestination.icon),
                    contentDescription = aboutDestination.title,
                    modifier = Modifier.size(28.dp)
                )
            }
        )
    }
}

