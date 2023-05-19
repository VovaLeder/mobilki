package com.example.mobilki.presentation.screens.auth_screen.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.room.Room
import com.example.mobilki.R
import com.example.mobilki.data.AppDatabase
import com.example.mobilki.data.models.UserModel
import com.example.mobilki.presentation.components.PasswordInput
import com.example.mobilki.presentation.components.PhoneInput
import com.example.mobilki.presentation.dim.Dimens
import com.example.mobilki.ui.theme.typography
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Timestamp
import java.util.*

@Composable
fun RegistrationScreen() {
    val code = remember { mutableStateOf("") }
    val number = remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val pass = remember { mutableStateOf("") }
    val confirmPass = remember { mutableStateOf("") }

    val applicationContext = LocalContext.current

    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()

    val rememberCoroutineScope = rememberCoroutineScope();
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.Paddings.basePadding),
        modifier = Modifier.fillMaxSize()
    ) {
        PhoneInput(phoneCodeState = code, phoneNumberState = number)

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text(text = stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth()
                .then(Dimens.Modifiers.commonModifier)
        )

        PasswordInput(passState = pass, stringResource(R.string.password))

        PasswordInput(passState = confirmPass, stringResource(R.string.password_confirmation))

        var job: Job? by remember {
            mutableStateOf(null)
        }
        Button(
            onClick = {
                job = rememberCoroutineScope.launch {
                    if (code.value == "" || number.value == "" || name == "" || pass.value == "" || confirmPass.value == ""){
                        Toast.makeText(applicationContext, "Input Every Field.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    if (pass.value != confirmPass.value){
                        Toast.makeText(applicationContext, "Pass and ConfirmPass field has different values", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val user = db.userDao().findByNumber(number.value)
                    if (user != null){
                        Toast.makeText(applicationContext, "User with that number already exists", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    db.userDao().insertAll(
                        UserModel(
                            uid = 0,

                            authKey = UUID.randomUUID().toString(),
                            expireDate = Timestamp(System.currentTimeMillis()),

                            phoneCode = code.value,
                            phoneNumber = number.value,
                            name = name,
                            pass = pass.value,
                            admin = false,
                        )
                    )

                    Toast.makeText(applicationContext, "Now go to the login page to login.", Toast.LENGTH_SHORT).show()
                }

            },
            shape = RoundedCornerShape(Dimens.Shapes.baseBorderShape),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Paddings.halfPadding)
        ) {
            Text(
                text = stringResource(id = R.string.continue_string),
                style = typography.body1,
                color = Color.White,
            )
        }
    }
}
