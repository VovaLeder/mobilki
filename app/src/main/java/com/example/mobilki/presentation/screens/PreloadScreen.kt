package com.example.mobilki.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.mobilki.data.AppDatabase
import com.example.mobilki.ui.theme.typography
import com.example.mobilki.utils.objects.SessionParameters
import kotlinx.coroutines.launch
import java.sql.Time
import java.sql.Timestamp

@Composable
fun PreloadScreen() {
    val applicationContext = LocalContext.current
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()

    Column() {
        Text(
            text = "Loading",
            style = typography.body1,
            color = Color.White,
        )
    }

    val rememberCoroutineScope = rememberCoroutineScope();
    LaunchedEffect(key1 = true){
        rememberCoroutineScope.launch {
            val user = db.userDao().findByAuthKey(SessionParameters.getAuthKey())

            if (user == null || user.expireDate.before(Timestamp(System.currentTimeMillis()))){
                SessionParameters.setAuthKey("", applicationContext)
                SessionParameters.getNavController().navigate(NavHostRoutes.AuthScreen.name)
                return@launch
            }

            if (user.admin){
                SessionParameters.users = db.userDao().getAll()
            }

            user.expireDate = Timestamp(System.currentTimeMillis() + 3_600_000)
            db.userDao().updateUser(user)

            SessionParameters.currentUser = user

            SessionParameters.getNavController().navigate(NavHostRoutes.GreetUserScreen.name)
        }
    }
}

