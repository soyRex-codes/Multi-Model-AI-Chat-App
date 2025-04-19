package com.example.csc567_project

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.csc567_project.data.local.ChatDatabase
import com.example.csc567_project.data.local.ModelRegistry
import com.example.csc567_project.datastore.ApiKeyStore
import com.example.csc567_project.model.Conversation
import com.example.csc567_project.ui.ApiKeyEntryDialog
import com.example.csc567_project.ui.theme.NavyBlue
import com.example.csc567_project.viewmodel.TopBarViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController, topBarViewModel: TopBarViewModel) {
    // Set top bar properties
    topBarViewModel.setTitle("Click a chat to view/resume the conversation with any Model")
    topBarViewModel.setTitleColor(Color.Black)
    topBarViewModel.setNavColor(Color.Black)
    topBarViewModel.setColor(Color.Transparent)

    val context = LocalContext.current
    val db = remember { ChatDatabase.getDatabase(context) }
    val conversationDao = db.conversationDao()

    val conversationEntitiesFlow = remember { conversationDao.getAllConversations() }
    val conversationEntities by conversationEntitiesFlow.collectAsState(initial = emptyList())

    val conversationList = remember(conversationEntities) {
        conversationEntities.map {
            Conversation(
                id = it.conversationId,
                title = it.title,
                model = it.model,
                date = SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date(it.timestamp))
            )
        }
    }

    val groupedConversations = conversationList.groupBy { it.date }

    val apiKeyStore = remember { ApiKeyStore(context) }
    val coroutineScope = rememberCoroutineScope()

    val modelNames = ModelRegistry.getModelNames()
    var selectedModelName by remember { mutableStateOf(modelNames.first()) }
    val selectedModelId = ModelRegistry.getModelId(selectedModelName)
    var showApiKeyDialogNewChat by remember { mutableStateOf(false) }

    // State to track API key dialog visibility
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var selectedConversationId by remember { mutableStateOf< Int?>(null) }
    var apiKey by remember { mutableStateOf("") }
    var apiKeyInputExistingChat by remember { mutableStateOf("") }
    var apiKeyInputNewChat by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Select a Model:",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var expanded by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedModelName,
                            onValueChange = {},
                            label = { Text("AI Model") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            modelNames.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedModelName = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .width(90.dp)
                            .background(NavyBlue)
                            .clickable { showApiKeyDialogNewChat = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Enter",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Text(
                    text = "Click me to Get Free API Key from AIMLAPI\n Detail Steps: Signup/Signin -> top right menu -> Key Management -> Create API Key",
                    color = Color.Red,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, "https://aimlapi.com/app/keys".toUri())
                            context.startActivity(intent)
                        }
                )
            }
        }
        // For each date group, add a header and the conversation items
        groupedConversations.forEach { (date, convList) ->
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = date, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                }
            }
            items(convList) { conversation ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(NavyBlue)
                        .clickable(onClick = {
                            selectedConversationId = conversation.id

                            coroutineScope.launch {
                                val savedKey = apiKeyStore.getApiKey(conversation.model).first()
                            if (savedKey.isNotBlank()) {
                                    navController.navigate("chat/${conversation.id}/${conversation.model}")
                                } else {
                                    showApiKeyDialog = true
                                }
                            }
                        })
                ) {
                    Text(
                        text = "${conversation.title} - ${conversation.model}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // API Key Dialog
    if (showApiKeyDialog) {
        ApiKeyEntryDialog(
            showDialog = true,
            modelName = selectedModelId,
            onDismiss = {
                showApiKeyDialog = false
            },
            onConfirm = { apiKey ->
                val model = selectedModelId
                showApiKeyDialog = false
                coroutineScope.launch {
                    apiKeyStore.saveApiKey(model, apiKey)
                }
                selectedConversationId?.let {
                    navController.navigate("chat/$it/$model")
                }
            },
            apiKeyInput = apiKeyInputExistingChat,
            onApiKeyChange = { apiKeyInputExistingChat = it },
            enableConfirm = apiKeyInputExistingChat.isNotBlank()
        )
    }

    if (showApiKeyDialogNewChat) {
        ApiKeyEntryDialog(
            showDialog = true,
            modelName = selectedModelId,
            onDismiss = { showApiKeyDialogNewChat = false },
            onConfirm = { apiKey ->
                showApiKeyDialogNewChat = false
                coroutineScope.launch {
                    apiKeyStore.saveApiKey(selectedModelId, apiKey)
                }
                val conversationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                navController.navigate("chat/$conversationId/$selectedModelId")
            },
            apiKeyInput = apiKeyInputNewChat,
            onApiKeyChange = { apiKeyInputNewChat = it },
            enableConfirm = apiKeyInputNewChat.isNotBlank()
        )
    }
}