package org.halfline.kt_meme_manager.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Welcome to Meme Manager",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "This is a simple application to manage your memes.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ManageAccounts,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).padding(end = 16.dp)
                )
                Column {
                    Text(
                        "Manage Memes", style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Add, edit, and organize your memes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).padding(end = 16.dp)
                )
                Column {
                    Text(
                        "Settings", style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Customize your meme manager experience",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}