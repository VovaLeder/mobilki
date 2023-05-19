package com.example.mobilki.presentation.screens.auth_screen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.room.Room
import com.example.mobilki.R
import com.example.mobilki.data.AppDatabase
import com.example.mobilki.data.models.UserModel
import com.example.mobilki.presentation.components.PasswordInput
import com.example.mobilki.presentation.dim.Dimens
import com.example.mobilki.presentation.screens.NavHostRoutes
import com.example.mobilki.ui.theme.typography
import kotlinx.coroutines.launch
import androidx.compose.material.TextField
import com.example.mobilki.utils.objects.SessionParameters

@Composable
fun ChangeScreen() {
    val navController = SessionParameters.getNavController()
    val authKey = SessionParameters.getAuthKey()

    var name by remember { mutableStateOf("") }
    val pass = remember { mutableStateOf("") }

    var user: UserModel? by remember {mutableStateOf(null)}
    user = SessionParameters.currentUser

    val applicationContext = LocalContext.current

    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()

    val rememberCoroutineScope = rememberCoroutineScope();

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.Paddings.basePadding),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.Paddings.basePadding)
    ) {
        user?.phoneNumber?.let {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Paddings.halfPadding),
                modifier = Modifier.fillMaxWidth()
                    ) {
                Text(
                    text = "Phone Number: ",
                    style = typography.body1
                )
                Text(
                    text = it,
                    style = typography.body1
                )
            }
        }

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text(text = "Имя") },
            modifier = Modifier
                .fillMaxWidth()
                .then(Dimens.Modifiers.commonModifier)
        )

        PasswordInput(passState = pass, stringResource(R.string.password))

        Button(
            onClick = {
                rememberCoroutineScope.launch {
                    val userOnClick = db.userDao().findByAuthKey(authKey)

                    userOnClick.name = name
                    userOnClick.pass = pass.value

                    SessionParameters.currentUser = userOnClick
                    db.userDao().updateUser(userOnClick)

                    navController.navigate(
                        route = NavHostRoutes.GreetUserScreen.name,
                    )
                }
            },
            shape = RoundedCornerShape(Dimens.Shapes.baseBorderShape),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Paddings.halfPadding)
        ) {
            Text(
                text = "Change",
                style = typography.body1,
                color = Color.White
            )
        }
    }
}
