package com.example.csc567_project

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csc567_project.data.local.ChatDatabase
import com.example.csc567_project.datastore.ApiKeyStore
import com.example.csc567_project.datastore.SettingsStore
import com.example.csc567_project.viewmodel.ThemeViewModel
import com.example.csc567_project.viewmodel.TopBarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(topBarViewModel: TopBarViewModel, themeViewModel: ThemeViewModel) {
    topBarViewModel.setTitle("Settings")
    topBarViewModel.useDefaultColors()

    val darkModeEnabled by themeViewModel.isDarkTheme
    var temperature by remember { mutableFloatStateOf(50f) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SettingItem(
                icon = Icons.Default.BrightnessMedium,
                title = "Theme Switch",
                subtitle = if (darkModeEnabled) "Dark Mode" else "Light Mode"
            ) {
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = { themeViewModel.toggleTheme(it) }
                )
            }
        }
        item {
            var expanded by remember { mutableStateOf(false) }
            var showAPIUrl by remember { mutableStateOf(false) }
            var apiUrl by remember { mutableStateOf("") }
            val models = listOf("AIMLAPI", "OpenAI", "Ollama")
            val settingsStore = remember { SettingsStore(context) }
            val apiProvider by settingsStore.getAPIProvider().collectAsState(initial = models.first())

            SettingItem(
                icon = Icons.Default.Api,
                title = "API Provider",
                subtitle = "Select <user__selection></user__selection>the API service to use"
            ) {
                Column(
                    modifier = Modifier.width(125.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = apiProvider,
                            onValueChange = {},
                            label = { Text("Service") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            models.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        if (selectionOption == "Ollama") {
                                            showAPIUrl = true;
                                        }

                                        coroutineScope.launch {
                                            settingsStore.setAPIProvider(selectionOption)
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showAPIUrl) {
                AlertDialog(
                    onDismissRequest = {showAPIUrl = false},
                    title = { Text(text = "Enter Ollama API URL") },
                    text = {
                        Column {
                            Text(text = "Please enter the URL for your Ollama instance:")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = apiUrl,
                                onValueChange = { apiUrl = it },
                                label = { Text("API URL") }
                            )
                            if(apiUrl.isBlank()){
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Warning: API URL cannot be blank", color = Color.Red)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    settingsStore.setAPIUrl(apiUrl)
                                }
                                showAPIUrl = false
                            },
                            enabled = apiUrl.isNotBlank()
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            Toast.makeText(context, "Ollama requires a valid API URL to work.", Toast.LENGTH_LONG).show()
                            showAPIUrl = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        item {
            SettingItem(
                icon = Icons.Default.Thermostat,
                title = "Temperature",
                subtitle = "Creativity"
            ) {
                Column(
                    modifier = Modifier.width(150.dp)
                ) {
                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "${temperature.toInt()}%") // Convert to Int for display
                }
            }
        }
        item {
            SettingItem(
                icon = Icons.Default.Delete,
                title = "Delete All Chats",
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = ChatDatabase.getDatabase(context)
                        db.chatDao().deleteAllMessages()
                        db.conversationDao().deleteAllConversations()
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "All chats deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp)
            subtitle?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
        }
        trailingContent?.invoke()
    }
}