package com.example.mobilki.utils.objects

import android.annotation.SuppressLint
import android.content.Context
import androidx.navigation.NavController
import com.example.mobilki.data.models.UserModel
import java.sql.Timestamp

@SuppressLint("StaticFieldLeak")
object SessionParameters {
    var currentUser: UserModel = UserModel(
        uid = 0,

        authKey = "",
        expireDate = Timestamp(System.currentTimeMillis()),

        phoneCode = "",
        phoneNumber = "",
        name = "",
        pass = "",

        admin = false,
    );

    var users: List<UserModel> = mutableListOf()

    fun resetUser(){
        currentUser = UserModel(
            uid = 0,

            authKey = "",
            expireDate = Timestamp(System.currentTimeMillis()),

            phoneCode = "",
            phoneNumber = "",
            name = "",
            pass = "",

            admin = false,
        );
    }

    private var authKey: String = ""
    private lateinit var navController: NavController

    private var sessionTime: Long = 0

    fun setAuthKey(authKey: String, context: Context){
        this.authKey = authKey
        val sharedPref  = context.getSharedPreferences("com.example.myapp.PREFERENCE_FILE", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("authKey", authKey)
            apply()
        }
    }

    fun setSessionTime() {
        sessionTime = System.currentTimeMillis()
    }

    fun checkSessionTime(): Boolean{
        return sessionTime + 60*60_000 > System.currentTimeMillis()
    }

    fun getAuthKey(): String {
        return authKey
    }

    fun getNavController(): NavController {
        return navController
    }

    fun init(context: Context, navController: NavController){
        val sharedPref  = context.getSharedPreferences("com.example.myapp.PREFERENCE_FILE", Context.MODE_PRIVATE) ?: return
        authKey = sharedPref.getString("authKey", "") ?: ""

        this.navController = navController

        sessionTime = System.currentTimeMillis()
    }
}