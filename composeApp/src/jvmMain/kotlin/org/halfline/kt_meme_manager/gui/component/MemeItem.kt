package org.halfline.kt_meme_manager.gui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.halfline.kt_meme_manager.core.Meme
import java.awt.Toolkit
import java.io.File
import javax.imageio.ImageIO

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MemeItem(
    meme: Meme,
    onCopy: (ImageBitmap) -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var imageBitmap by remember(meme.file) { mutableStateOf<ImageBitmap?>(null) }
    var isHovered by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(meme.file) {
        imageBitmap = withContext(Dispatchers.IO) {
            val image = ImageIO.read(meme.file)
            image?.toComposeImageBitmap()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .onPointerEvent(PointerEventType.Enter) {
                isHovered = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                isHovered = false
            }
            .clickable {
                // 复制图片到剪贴板
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        imageBitmap?.let { bitmap ->
                            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                            val transferable = ImageTransferable(bitmap.toAwtImage())
                            clipboard.setContents(transferable, null)

                            // 设置复制状态用于UI反馈
                            withContext(Dispatchers.Main) {
                                isCopied = true

                                // 延迟重置复制状态
                                launch {
                                    kotlinx.coroutines.delay(1000)
                                    isCopied = false
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // 静默处理异常
                    }
                }
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                MemeImage(file = meme.file)

                if (isHovered) {
                    MemeItemButtons(
                        imageBitmap = imageBitmap,
                        onEditClick = onEdit,
                        onDeleteClick = onDelete,
                        isCopied = isCopied,
                        onCopySuccess = { isCopied = true },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .zIndex(1f)
                    )
                }
            }

            Text(
                text = meme.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )

            Text(
                text = meme.collection,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun MemeImage(file: File) {
    var imageBitmap by remember(file) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(file) { mutableStateOf(true) }

    LaunchedEffect(file) {
        isLoading = true
        imageBitmap = withContext(Dispatchers.IO) {
            val image = ImageIO.read(file)
            image?.toComposeImageBitmap()
        }.also {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Image,
                        contentDescription = "Failed to load image",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Failed to load image",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}