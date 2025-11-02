package org.halfline.kt_meme_manager.gui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

@Composable
fun MemeItemButtons(
    imageBitmap: ImageBitmap?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCopied: Boolean = false,
    onCopySuccess: () -> Unit = {}
) {
    var internalIsCopied by remember { mutableStateOf(false) }
    // 使用外部传入的 isCopied 状态（如果为 true）或者内部状态
    val actualIsCopied = if (isCopied) true else internalIsCopied
    // 修复类型不匹配问题
    val actualOnCopySuccess = if (isCopied) onCopySuccess else {
        { internalIsCopied = true }
    }

    Row(
        modifier = modifier
            .zIndex(1f),
    ) {
        CopyButton(
            imageBitmap = imageBitmap,
            isCopied = actualIsCopied,
            onCopySuccess = actualOnCopySuccess,
            onResetCopyState = { internalIsCopied = false }
        )

        EditButton(
            onClick = onEditClick
        )

        DeleteButton(
            onClick = onDeleteClick
        )
    }
}

@Composable
private fun CopyButton(
    imageBitmap: ImageBitmap?,
    isCopied: Boolean,
    onCopySuccess: () -> Unit,
    onResetCopyState: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    imageBitmap?.let { bitmap ->
                        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                        val transferable = ImageTransferable(bitmap.toAwtImage())
                        clipboard.setContents(transferable, null)

                        withContext(Dispatchers.Main) {
                            onCopySuccess()
                            launch {
                                kotlinx.coroutines.delay(1000)
                                onResetCopyState()
                            }
                        }
                    }
                } catch (e: Exception) {
                    // 静默处理异常
                }
            }
        },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White.copy(alpha = 0.9f),
            contentColor = if (isCopied) MaterialTheme.colorScheme.primary else Color.Black
        ),
        modifier = Modifier.size(36.dp)
    ) {
        Icon(
            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
            contentDescription = if (isCopied) "Copied" else "Copy",
            tint = if (isCopied) MaterialTheme.colorScheme.onPrimary else Color.Black,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EditButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White.copy(alpha = 0.8f),
            contentColor = Color.Black
        ),
        modifier = Modifier.size(36.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DeleteButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        modifier = Modifier.size(36.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(20.dp)
        )
    }
}

internal class ImageTransferable(private val image: Image) : Transferable {
    @Throws(UnsupportedFlavorException::class)
    override fun getTransferData(flavor: DataFlavor): Any {
        if (isDataFlavorSupported(flavor)) {
            return image
        } else {
            throw UnsupportedFlavorException(flavor)
        }
    }

    override fun isDataFlavorSupported(flavor: DataFlavor) = flavor === DataFlavor.imageFlavor

    override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
}