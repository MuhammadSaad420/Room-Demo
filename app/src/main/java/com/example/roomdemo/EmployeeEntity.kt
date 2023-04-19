package com.example.roomdemo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee-table")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    private val id: Int = 0,
    private val name: String = "",
    @ColumnInfo(name="email-id")
    private val email: String = ""
)
