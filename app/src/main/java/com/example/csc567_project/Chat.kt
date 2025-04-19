package com.example.csc567_project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.csc567_project.api.AIClient
import com.example.csc567_project.data.local.ChatDatabase
import com.example.csc567_project.data.local.ChatMessageEntity
import com.example.csc567_project.viewmodel.TopBarViewModel
import kotlinx.coroutines.launch
import com.example.csc567_project.ui.MessageBoxElement as MessageBoxElement
import com.example.csc567_project.datastore.ApiKeyStore
import com.example.csc567_project.datastore.SettingsStore

@Composable
fun Chat(topBarViewModel: TopBarViewModel, conversationId: Int, model: String) {
    val context = LocalContext.current
    val db = remember { ChatDatabase.getDatabase(context) }
    val chatDao = db.chatDao()
    val scope = rememberCoroutineScope()

    var showSaveDialog by remember { mutableStateOf(false) }
    var chatTitle by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Top app bar styling
    // Setting top bar properties based on the conversation
    topBarViewModel.setTitle("Chat - Conversation #$conversationId")
    topBarViewModel.setTitleColor(Color.Black)
    topBarViewModel.setNavColor(Color.Black)
    topBarViewModel.setColor(Color.Transparent)

    val messages by chatDao.observeMessagesForConversation(conversationId)
        .collectAsState(initial = emptyList())

    val listState = rememberLazyListState()
    val apiKeyStore = remember { ApiKeyStore(context) }
    val settingsStore = remember { SettingsStore(context) }

    val savedKey by apiKeyStore.getApiKey(model).collectAsState(initial = "")
    val apiProvider by settingsStore.getAPIProvider().collectAsState(initial = "")
    val apiUrlOverride by settingsStore.getAPIUrl().collectAsState(initial = "")

    /* For testing purposes http://10.0.2.2:11434/v1/ is the URL for local Ollama debugging */
    val apiUrl = if (apiProvider == "Ollama") {
        apiUrlOverride
    } else {
    "https://api.aimlapi.com/v1" // AIMLAPI root URL
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Model: ${messages.firstOrNull { it.isAI }?.model ?: model}",
                    color = Color.DarkGray
                )
                Text(
                    text = "Save Chat",
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        showSaveDialog = true
                    }
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Draw each chat message
                items(messages) { entity: ChatMessageEntity ->
                    MessageBoxElement(message = entity.message, isAI = entity.isAI)
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                var prompt by remember { mutableStateOf("") }

                if (isLoading) {
                    Text("Thinking...", modifier = Modifier.padding(8.dp), color = Color.Gray)
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = prompt,
                    label = { Text("Prompt") },
                    onValueChange = { prompt = it },
                    placeholder = { Text("Enter your question") },
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            Icons.AutoMirrored.Sharp.ArrowForward,
                            contentDescription = "Send",
                            modifier = Modifier.clickable(onClick = {
                                scope.launch {
                                    if (savedKey.isBlank()) {
                                        android.widget.Toast.makeText(context, "API key for $model is missing!", android.widget.Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    isLoading = true
                                    val client = AIClient(apiKey = savedKey, model = model, baseUrl = apiUrl)
                                    val userMessage = prompt.trim()
                                    if (userMessage.isNotEmpty()) {
                                        chatDao.insertMessage(
                                            ChatMessageEntity(
                                                conversationId = conversationId,
                                                message = userMessage,
                                                isAI = false,
                                                timestamp = System.currentTimeMillis(),
                                                model = client.getModel()
                                            )
                                        )

                                        try {
                                            val result = client.sendChat(userMessage)
                                            val responseText = result.choices.firstOrNull()?.message?.content ?: "No response"

                                            chatDao.insertMessage(
                                                ChatMessageEntity(
                                                    conversationId = conversationId,
                                                    message = responseText,
                                                    isAI = true,
                                                    timestamp = System.currentTimeMillis(),
                                                    model = client.getModel()
                                                )
                                            )

                                            prompt = ""
                                        } catch (e: Exception) {
                                            if (e is kotlinx.coroutines.CancellationException) {
                                                println("Chat request cancelled.")
                                            } else {
                                                println("Error during chat completion: ${e.message}")
                                            }
                                        } finally {
                                            isLoading = false
                                        }
                                    } else {
                                        isLoading = false
                                    }
                                }
                            })
                        )
                    }
                )
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Chat") },
            text = {
                Column {
                    Text("Enter a title for this chat session:")
                    OutlinedTextField(
                        value = chatTitle,
                        onValueChange = { chatTitle = it },
                        label = { Text("Chat Title") }
                    )
                }
            },
            confirmButton = {
                Text(
                    "Save",
                    modifier = Modifier.clickable {
                        scope.launch {
                            db.conversationDao().insertConversation(
                                com.example.csc567_project.data.local.ConversationEntity(
                                    conversationId = conversationId,
                                    title = if (chatTitle.isNotBlank()) chatTitle else "Untitled Chat",
                                    model = messages.firstOrNull { it.isAI }?.model ?: model,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            android.widget.Toast
                                .makeText(context, "Chat saved successfully", android.widget.Toast.LENGTH_SHORT)
                                .show()
                        }
                        showSaveDialog = false
                    }
                )
            },
            dismissButton = {
                Text(
                    "Cancel",
                    modifier = Modifier.clickable {
                        showSaveDialog = false
                    }
                )
            }
        )
    }
}