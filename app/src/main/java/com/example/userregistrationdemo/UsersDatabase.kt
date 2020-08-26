package com.example.userregistrationdemo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class), version = 2)
abstract class UsersDatabase : RoomDatabase() {
    companion object{
        private val DB_NAME: String = "users_db"
        private lateinit var instance: UsersDatabase

        public fun getInstance(context: Context): UsersDatabase{
            instance = Room.databaseBuilder(context.applicationContext,UsersDatabase::class.java, DB_NAME).fallbackToDestructiveMigration().build()
            return instance
        }
    }

    abstract fun userDao() : UserDao
}