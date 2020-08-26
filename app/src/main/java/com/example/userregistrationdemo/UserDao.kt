package com.example.userregistrationdemo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE name = :name")
    fun loadByName(name: String): User

    @Query("SELECT name FROM user ORDER BY LOWER(name) ASC")
    fun sortedFind(): List<String>

    @Query("DELETE FROM user")
    fun nukeTable()

    @Insert
    fun insertUser(user: User)

    @Delete
    fun delete(user: User)
}