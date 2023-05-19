package com.example.mobilki.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mobilki.data.models.UserDao
import com.example.mobilki.data.models.UserModel
import com.example.mobilki.data.type_converters.TimeStampTypeConverter

@Database(entities = [UserModel::class], version = 1)
@TypeConverters(TimeStampTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
