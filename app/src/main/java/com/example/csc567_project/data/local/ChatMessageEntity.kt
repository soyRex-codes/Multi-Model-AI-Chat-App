package com.example.csc567_project.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "conversationId") val conversationId: Int,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "isAI") val isAI: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "model") val model: String
)