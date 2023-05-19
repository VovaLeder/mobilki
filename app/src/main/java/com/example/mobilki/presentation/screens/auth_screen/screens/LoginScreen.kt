package com.example.mobilki.presentation.screens.auth_screen.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import com.example.mobilki.presentation.components.PasswordInput
import com.example.mobilki.presentation.components.PhoneInput
import com.example.mobilki.presentation.dim.Dimens
import com.example.mobilki.presentation.screens.NavHostRoutes
import com.example.mobilki.ui.theme.typography
import com.example.mobilki.utils.objects.SessionParameters
import kotlinx.coroutines.launch
import java.sql.Timestamp

@Composable
fun LoginScreen() {
    val navController = SessionParameters.getNavController()

    val code = remember { mutableStateOf("") }
    val number = remember { mutableStateOf("") }
    val pass = remember { mutableStateOf("") }

    val applicationContext = LocalContext.current

    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.Paddings.basePadding),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.Paddings.basePadding)
    ) {
        PhoneInput(phoneCodeState = code, phoneNumberState = number)

        PasswordInput(passState = pass, stringResource(R.string.password))

        val rememberCoroutineScope = rememberCoroutineScope();
        var isRememberMe by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Paddings.halfPadding),
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = isRememberMe, onCheckedChange = { isRememberMe = !isRememberMe })

            Text(
                text = stringResource(id = R.string.keep_me_logged_in),
                style = typography.body1
            )

            TextButton(
                onClick = {
                    Toast.makeText(applicationContext, "Forgor", Toast.LENGTH_SHORT).show()
                },
            ) {
                Text(
                    text = stringResource(id = R.string.forgotPassword),
                    style = typography.body1,
                    color = Color.Blue
                )
            }
        }

        Button(
            onClick = {
                rememberCoroutineScope.launch {
                    val user = db.userDao().findByNumber(number.value)

                    if (user == null){
                        Toast.makeText(applicationContext, "User does not exist", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (user.pass != pass.value){
                        Toast.makeText(applicationContext, "Wrong password", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    user.expireDate = Timestamp(System.currentTimeMillis() + 3_600_000)
                    db.userDao().updateUser(user)

                    SessionParameters.setAuthKey(user.authKey, applicationContext)
                    navController.navigate(
                        route = NavHostRoutes.PreloadScreen.name,
                    )
                }
            },
            shape = RoundedCornerShape(Dimens.Shapes.baseBorderShape),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Paddings.halfPadding)
        ) {
            Text(
                text = stringResource(id = R.string.login),
                style = typography.body1,
                color = Color.White
            )
        }
    }
}
