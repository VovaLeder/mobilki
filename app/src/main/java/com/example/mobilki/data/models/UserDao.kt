package com.example.mobilki.data.models

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserModel>

    @Query("SELECT * FROM users WHERE phoneNumber = :first LIMIT 1")
    suspend fun findByNumber(first: String): UserModel

    @Query("SELECT * FROM users WHERE uid = :first LIMIT 1")
    suspend fun findById(first: String): UserModel

    @Query("SELECT * FROM users WHERE authKey = :first LIMIT 1")
    suspend fun findByAuthKey(first: String): UserModel

    @Update
    suspend fun updateUser(user: UserModel)

    @Insert
    suspend fun insertAll(vararg users: UserModel)

    @Delete
    suspend fun delete(user: UserModel)
}
