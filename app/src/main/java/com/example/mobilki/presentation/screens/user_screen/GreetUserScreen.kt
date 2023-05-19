package com.example.mobilki.presentation.screens.user_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.mobilki.data.AppDatabase
import com.example.mobilki.data.models.UserModel
import com.example.mobilki.presentation.dim.Dimens
import com.example.mobilki.presentation.screens.NavHostRoutes
import com.example.mobilki.ui.theme.typography
import com.example.mobilki.utils.objects.SessionParameters
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GreetUserScreen() {
    val navController = SessionParameters.getNavController()
    val authKey = SessionParameters.getAuthKey()

    val applicationContext = LocalContext.current
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()

    val rememberCoroutineScope = rememberCoroutineScope();
    var user: UserModel? by remember {mutableStateOf(null)}
    user = SessionParameters.currentUser

    var users: List<UserModel>? by remember { mutableStateOf(null) }

    Column() {
        Button(
            onClick = {
                rememberCoroutineScope.launch {
                    navController.navigate(
                        route = NavHostRoutes.ChangeUserInfoScreen.name
                    )
                }
            },
            shape = RoundedCornerShape(Dimens.Shapes.baseBorderShape),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Paddings.halfPadding)
        ) {
            Text(
                text = "Hello ${user?.name}",
                style = typography.body1,
                color = Color.White,
            )
        }

        Button(
            onClick = {
                SessionParameters.setAuthKey("", applicationContext)
                SessionParameters.resetUser()

                rememberCoroutineScope.launch {
                    navController.navigate(
                        route = NavHostRoutes.AuthScreen.name
                    )
                }
            },
            shape = RoundedCornerShape(Dimens.Shapes.baseBorderShape),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Paddings.halfPadding)
        ) {
            Text(
                text = "Log out",
                style = typography.body1,
                color = Color.White,
            )
        }

        if (user?.admin == true) {

            Text(
                text = "Admin Panel",
                style = typography.body1,
            )

            for (user_ in SessionParameters.users){
                Button(
                    onClick = {
                        rememberCoroutineScope.launch {
                            user_.admin = !user_.admin
                            db.userDao().updateUser(user_)
                        }
                    },
                    shape = RoundedCornerShape(Dimens.Shapes.baseBorderShape),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Paddings.halfPadding)
                ) {
                    Text(
                        text = "${user_.name}",
                        style = typography.body1,
                        color = Color.White,
                    )
                }
            }
        }
        else {
            Text(text = "User")
        }

    }
}

