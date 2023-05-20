package com.example.mobilki.presentation.screens.user_screen

import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.room.Room
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.mobilki.data.AppDatabase
import com.example.mobilki.data.models.UserModel
import com.example.mobilki.presentation.dim.Dimens
import com.example.mobilki.presentation.screens.NavHostRoutes
import com.example.mobilki.ui.theme.typography
import com.example.mobilki.utils.objects.SessionParameters
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.*
import java.io.IOException
import java.net.URL
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GreetUserScreen() {
    val navController = SessionParameters.getNavController()

    val applicationContext = LocalContext.current
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()

    val rememberCoroutineScope = rememberCoroutineScope();
    var user: UserModel? by remember {mutableStateOf(null)}
    user = SessionParameters.currentUser


    var cryptoSymbol by remember { mutableStateOf("") }
    var cryptoName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(0.0) }
    var change by remember { mutableStateOf(0.0) }
    var logo_url by remember { mutableStateOf("") }

    var image: ImageView? by remember { mutableStateOf(null) }

    var error by remember { mutableStateOf(false) }


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

            for (user_ in SessionParameters.users) {
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
        } else {
            Text(text = "Enter crypto symbol")
            TextField(
                value = cryptoSymbol,
                onValueChange = { cryptoSymbol = it },
                placeholder = { Text(text = "BTC") },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(Dimens.Modifiers.commonModifier)
            )
            Button(
                onClick = {
                    rememberCoroutineScope.launch {
                        cryptoSymbol = cryptoSymbol.uppercase(Locale.ROOT)

                        var client = OkHttpClient()
                        var apiUrl = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest?symbol=$cryptoSymbol"
                        var request = Request.Builder()
                            .addHeader("X-CMC_PRO_API_KEY", "8661a296-94f9-4957-89f4-1c64a2e55aa2")
                            .addHeader("Accepts", "application/json")
                            .url(apiUrl).build()

                        client.newCall(request).enqueue(
                            object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    println("Failed$e")
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    val body = Json.parseToJsonElement("${response.body?.string()}").jsonObject
                                    if (body.containsKey("data") && body["data"]?.jsonObject?.get(cryptoSymbol)?.jsonArray?.size!! > 0){
                                        println("Body :${body}")
                                        error = false

                                        cryptoName = "${body["data"]
                                            ?.jsonObject?.get(cryptoSymbol)
                                            ?.jsonArray?.get(0)
                                            ?.jsonObject?.get("name")}"

                                        val res = body["data"]
                                            ?.jsonObject?.get(cryptoSymbol)
                                            ?.jsonArray?.get(0)
                                            ?.jsonObject?.get("quote")
                                            ?.jsonObject?.get("USD")
                                        val pri = "${res?.jsonObject?.get("price")}"
                                        val cha = "${res?.jsonObject?.get("percent_change_24h")}"

                                        price = pri.toDouble()
                                        change = cha.toDouble()
                                    }
                                    else{
                                        println("Error :${body["status"]?.jsonObject?.get("error_message")}")
                                        error = true
                                    }
                                }
                            }
                        )

                        val client1 = OkHttpClient()
                        val apiUrl1 = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/info?symbol=$cryptoSymbol"
                        val request1 = Request.Builder()
                            .addHeader("X-CMC_PRO_API_KEY", "8661a296-94f9-4957-89f4-1c64a2e55aa2")
                            .addHeader("Accepts", "application/json")
                            .url(apiUrl1).build()

                        client1.newCall(request1).enqueue(
                            object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    println("Failed$e")
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    val body = Json.parseToJsonElement("${response.body?.string()}").jsonObject
                                    if (body.containsKey("data")){
                                        println("Body :${body}")

                                        logo_url = "${body["data"]
                                            ?.jsonObject?.get(cryptoSymbol)
                                            ?.jsonArray?.get(0)
                                            ?.jsonObject?.get("logo")}"

                                        logo_url = logo_url.substring(1, logo_url.length - 1)

                                        println("Image: $logo_url")
                                    }
                                    else{
                                        println("Error :${body["status"]?.jsonObject?.get("error_message")}")
                                    }
                                }
                            }
                        )
                    }
                },
                shape = RoundedCornerShape(Dimens.Shapes.baseBorderShape),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Paddings.halfPadding)
            ) {
                Text(
                    text = "Find",
                    style = typography.body1,
                    color = Color.White
                )
            }
            if (!error){
                Text(text = "Name: $cryptoName")
                AsyncImage(
                    model = logo_url,
                    contentDescription = "Crypto Logo",
                    modifier = Modifier.size(64.dp),
                    onError={println("AsyncImageOnError: ${it.result.throwable}")}
                )

                Text(text = "Price (USD): $price")
                Text(text = "24h change: $change%", color = if (change > 0) Color.Green else Color.Red)
            }
            else {
                Text(text = "Input Valid Crypto Symbol")
            }

        }
    }
}

@Composable
fun MyImage(url: String){
    println("MyImageFunc. $url")
    AsyncImage(
        model = url,
        contentDescription = "Crypto Logo",
        modifier = Modifier.size(64.dp),
    )
}

