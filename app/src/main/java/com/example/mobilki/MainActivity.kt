package com.example.mobilki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.mobilki.data.AppDatabase
import com.example.mobilki.presentation.screens.auth_screen.AuthPageScreen
import com.example.mobilki.presentation.screens.user_screen.GreetUserScreen
import com.example.mobilki.presentation.screens.NavHostRoutes
import com.example.mobilki.presentation.screens.PreloadScreen
import com.example.mobilki.presentation.screens.auth_screen.screens.ChangeScreen
import com.example.mobilki.ui.theme.Mobile3Theme
import com.example.mobilki.utils.objects.SessionParameters
import kotlinx.coroutines.launch
import java.sql.Timestamp

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Mobile3Theme {
                val context = LocalContext.current
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database-name"
                ).build()
                val rememberCoroutineScope = rememberCoroutineScope();

                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInteropFilter {
                            if (!SessionParameters.checkSessionTime()){
                                SessionParameters.setAuthKey("", context)
                                val navController = SessionParameters.getNavController()
                                navController.navigate(
                                    route = NavHostRoutes.PreloadScreen.name,
                                )
                            }
                            SessionParameters.setSessionTime()

                            rememberCoroutineScope.launch{
                                if (SessionParameters.getAuthKey() != ""){
                                    val user = db.userDao().findByAuthKey(SessionParameters.getAuthKey())
                                    user.expireDate = Timestamp(System.currentTimeMillis() + 3_600_000)
                                    db.userDao().updateUser(user)
                                }
                            }

                            false
                        },
                ) {
                    val navController = rememberNavController()
                    SessionParameters.init(LocalContext.current, navController)

                    NavHost(
                        navController = navController,
                        startDestination = NavHostRoutes.PreloadScreen.name,
                    ) {
                        composable(route = NavHostRoutes.AuthScreen.name) {
                            AuthPageScreen()
                        }
                        composable(route = NavHostRoutes.PreloadScreen.name){
                            PreloadScreen()
                        }
                        composable(route = NavHostRoutes.GreetUserScreen.name) {
                            GreetUserScreen()
                        }
                        composable(route = NavHostRoutes.ChangeUserInfoScreen.name){
                            ChangeScreen()
                        }
                    }
                }
            }
        }
    }
}
