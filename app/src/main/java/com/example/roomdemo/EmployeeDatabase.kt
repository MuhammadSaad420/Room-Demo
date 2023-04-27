package com.example.roomdemo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [EmployeeEntity::class], version = 1)
abstract class EmployeeDatabase: RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao

    companion object {
        @Volatile
        private var INSTANCE: EmployeeDatabase? = null
        fun getInstance(context: Context) : EmployeeDatabase? {
            var instance: EmployeeDatabase? = INSTANCE
            synchronized(this) {
                if(instance == null) {
                    instance = Room
                        .databaseBuilder(context.applicationContext,
                            EmployeeDatabase::class.java,
                            "employee_database")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance;
                }
                return instance;
            }
        }
    }
}