package com.example.csc567_project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AccountCircle
import androidx.compose.material.icons.sharp.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MessageBoxElement(message: String, isAI: Boolean) {
    if (message.isEmpty())
        return

    val backgroundColor = if (isAI) Color.LightGray else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(12.dp)
                .defaultMinSize(minHeight = 125.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                var iconToDraw = Icons.Sharp.AccountCircle
                if (isAI)
                    iconToDraw = Icons.Sharp.Face

                Icon(
                    iconToDraw,
                    contentDescription = "Sender",
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = message,
                    color = Color.Black,
                )
            }
        }
    }
}