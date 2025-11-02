package org.halfline.kt_meme_manager.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

class MemeViewModel : ViewModel() {
    private val repository = MemeRepository()
    private val memesDir = repository.memesDir

    var memes by mutableStateOf<List<Meme>>(emptyList())
        private set

    init {
        loadMemes()
        checkDataConsistency()
    }

    private fun loadMemes() {
        viewModelScope.launch {
            try {
                memes = repository.loadMemes()
            } catch (e: Exception) {
                // 忽略加载memes时的异常
            }
        }
    }

    fun addMeme(file: File, name: String, collection: String = "default") {
        viewModelScope.launch {
            try {
                val meme = Meme(
                    file = file,
                    name = name,
                    collection = collection
                )

                repository.addMeme(meme)
                loadMemes()
            } catch (e: Exception) {
                // 忽略添加meme时的异常
            }
        }
    }

    fun saveMeme(sourceFile: File, memeName: String? = null, collection: String = "default") {
        viewModelScope.launch {
            try {
                val hash = calculateFileHash(sourceFile)
                val extension = sourceFile.extension
                val name = if (!memeName.isNullOrBlank()) memeName else sourceFile.nameWithoutExtension
                val memeCollection = collection.ifBlank { "default" }

                val targetFileName = "$name-$hash.$extension"
                val targetFile = File(memesDir, targetFileName)

                Files.copy(
                    sourceFile.toPath(),
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )

                val meme = Meme(
                    file = targetFile,
                    name = name,
                    collection = memeCollection
                )

                repository.addMeme(meme)
                loadMemes()
            } catch (e: Exception) {
                // 忽略保存meme时的异常
            }
        }
    }

    private fun calculateFileHash(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { inputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        // 取前8位作为短哈希
        return digest.digest().joinToString("") { "%02x".format(it) }.take(8)
    }

    fun removeMeme(memeId: String) {
        viewModelScope.launch {
            try {
                val updatedMemes = memes.filter { it.id != memeId }
                repository.saveMemes(updatedMemes)
                memes = updatedMemes
            } catch (e: Exception) {
                // 忽略删除meme时的异常
            }
        }
    }

    fun updateMeme(memeId: String, name: String, collection: String) {
        viewModelScope.launch {
            try {
                val memeToUpdate = memes.find { it.id == memeId }
                if (memeToUpdate != null) {
                    val updatedMeme = memeToUpdate.copy(
                        name = name,
                        collection = collection,
                        updatedAt = java.time.LocalDateTime.now()
                    )
                    repository.updateMeme(memeId, updatedMeme)
                    loadMemes()
                }
            } catch (e: Exception) {
                // 忽略更新meme时的异常
            }
        }
    }

    fun checkDataConsistency() {
        viewModelScope.launch {
            try {
                val allMemes = repository.loadMemes()
                val allFiles = memesDir.listFiles() ?: return@launch

                val referencedFiles = allMemes.map { it.file }.toSet()
                val allMemeFiles = allFiles.filter { it.extension.isNotBlank() }.toSet()

                val unreferencedFiles = allMemeFiles - referencedFiles
                unreferencedFiles.forEach { file ->
                    file.delete()
                }
            } catch (e: Exception) {
                // 忽略清理时的异常
            }
        }
    }

    fun copyImageToClipboard(imageBitmap: ImageBitmap) {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val transferable = org.halfline.kt_meme_manager.gui.component.ImageTransferable(imageBitmap.toAwtImage())
            clipboard.setContents(transferable, null)
        } catch (e: Exception) {
            // 忽略复制到剪贴板时的异常
        }
    }

    fun copyPathToClipboard(filePath: String) {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val selection = StringSelection(filePath)
            clipboard.setContents(selection, null)
        } catch (e: Exception) {
            // 忽略复制到剪贴板时的异常
        }
    }
}