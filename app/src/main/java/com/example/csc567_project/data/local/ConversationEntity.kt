package com.example.csc567_project.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "conversationId") val conversationId: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)