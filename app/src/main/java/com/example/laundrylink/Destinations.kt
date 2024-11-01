package com.example.laundrylink

interface Destinations {
    val route: String
    val icon: Int
    val title: String
}

object Home : Destinations {
    override val route = "Home"
    override val icon = R.drawable.home
    override val title = "Home"
}

object History : Destinations {
    override val route = "History"
    override val icon = R.drawable.history
    override val title = "History"
}

object About : Destinations {
    override val route = "About"
    override val icon = R.drawable.about
    override val title = "About"
}

object User : Destinations {
    override val route = "User"
    override val icon = R.drawable.user
    override val title = "User"
}

object NewFeedback : Destinations {
    override val route = "NewFeedback"
    override val icon = R.drawable.feedback
    override val title = "Feedback"
}

object Login : Destinations {
    override val route = "Login"
    override val icon = R.drawable.feedback
    override val title = "Login"
}

object Signup : Destinations {
    override val route = "Signup"
    override val icon = R.drawable.feedback
    override val title = "Signup"
}

