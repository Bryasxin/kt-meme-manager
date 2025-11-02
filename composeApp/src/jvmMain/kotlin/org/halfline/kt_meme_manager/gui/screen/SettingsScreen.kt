package org.halfline.kt_meme_manager.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.halfline.kt_meme_manager.core.MemeViewModel

@Composable
fun SettingsScreen(viewModel: MemeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "Settings", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    "Storage", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Check Data Consistency")
                    Button(
                        onClick = { viewModel.checkDataConsistency() }) {
                        Text("Check Consistency")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    "About", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "Meme Manager v1.0",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    "A simple application to manage your memes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}