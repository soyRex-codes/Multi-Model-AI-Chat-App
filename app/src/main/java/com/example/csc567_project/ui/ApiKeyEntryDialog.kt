package com.example.csc567_project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.csc567_project.model.Conversation

@Composable
fun ApiKeyEntryDialog(
    showDialog: Boolean,
    modelName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    apiKeyInput: String,
    onApiKeyChange: (String) -> Unit,
    enableConfirm: Boolean
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Enter API Key") },
            text = {
                Column {
                    Text(text = "Please enter your $modelName API key below (Optional for Ollama):")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = apiKeyInput,
                        onValueChange = onApiKeyChange,
                        label = { Text("API Key") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(apiKeyInput)
                        onDismiss()
                    },
                    enabled = enableConfirm
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

