package org.halfline.kt_meme_manager.gui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.halfline.kt_meme_manager.core.Meme
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemeDrawer(
    selectedFile: File?,
    onFileSelected: (File) -> Unit,
    onDismiss: () -> Unit,
    onUpload: (String, String) -> Unit,
    memeToEdit: Meme? = null,
    onUpdate: ((String, String, String) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var memeName by remember { mutableStateOf(memeToEdit?.name ?: "") }
    var memeCollection by remember { mutableStateOf(memeToEdit?.collection ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (memeToEdit == null) "Add Meme" else "Edit Meme",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 仅在添加模式下显示文件选择框
            if (memeToEdit == null) {
                FileSelectionArea(
                    selectedFile = selectedFile, onFileSelected = onFileSelected
                )

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // 编辑模式下显示当前文件名
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(
                            "Current File:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            memeToEdit.file.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = memeName,
                onValueChange = { memeName = it },
                label = { Text("Meme Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = memeCollection,
                onValueChange = { memeCollection = it },
                label = { Text("Collection") },
                placeholder = { Text("default") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion { throwable ->
                            if (throwable == null) {
                                onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        val name = if (memeName.isBlank() && memeToEdit == null && selectedFile != null) {
                            selectedFile.nameWithoutExtension
                        } else if (memeName.isBlank() && memeToEdit != null) {
                            memeToEdit.name
                        } else {
                            memeName
                        }

                        val collection = memeCollection.ifBlank {
                            "default"
                        }

                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion { throwable ->
                            if (throwable == null) {
                                if (memeToEdit != null && onUpdate != null) {
                                    onUpdate(memeToEdit.id, name, collection)
                                } else {
                                    selectedFile?.let {
                                        onUpload(name, collection)
                                    }
                                }
                            }
                        }
                    },
                    enabled = if (memeToEdit != null) true else selectedFile != null
                ) {
                    Text(if (memeToEdit == null) "Save" else "Update")
                }
            }
        }
    }
}

@Composable
private fun FileSelectionArea(
    selectedFile: File?, onFileSelected: (File) -> Unit
) {
    val borderColor = if (selectedFile == null) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Gray
    }

    val isClickable = selectedFile == null

    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().border(
                width = 2.dp, color = borderColor, shape = MaterialTheme.shapes.medium
            ).background(Color.LightGray.copy(alpha = 0.2f)).clickable(enabled = isClickable) {
                openFileChooser { file ->
                    onFileSelected(file)
                }
            }, contentAlignment = Alignment.Center
        ) {
            if (selectedFile == null) {
                EmptyFileSelection()
            } else {
                SelectedFilePreview(fileName = selectedFile.name)
            }
        }
    }
}

private fun openFileChooser(onFileSelected: (File) -> Unit) {
    val fileChooser = JFileChooser()
    fileChooser.fileFilter = FileNameExtensionFilter(
        "Image files", "jpg", "jpeg", "png", "gif", "bmp"
    )
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        onFileSelected(fileChooser.selectedFile)
    }
}

@Composable
private fun EmptyFileSelection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Upload,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Select an image file", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            "Supports JPEG, PNG, GIF, BMP",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
private fun SelectedFilePreview(fileName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Selected File:", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            fileName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            "Click Save to add this meme",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}