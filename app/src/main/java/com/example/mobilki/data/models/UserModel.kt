package com.example.mobilki.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "users")
data class UserModel(
    @PrimaryKey(autoGenerate = true) val uid: Int,

    @ColumnInfo(name = "authKey") var authKey: String,
    @ColumnInfo(name = "expireData") var expireDate: Timestamp,

    @ColumnInfo(name = "phoneCode") val phoneCode: String?,
    @ColumnInfo(name = "phoneNumber") val phoneNumber: String?,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "pass") var pass: String?,

    @ColumnInfo(name = "admin") var admin: Boolean,
)
