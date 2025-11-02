package org.halfline.kt_meme_manager.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime

class MemeRepository {
    private val json = Json { prettyPrint = true }
    val storageDir = File(System.getProperty("user.home"), ".meme-manager")
    val dataFile = File(storageDir, "memes.json")
    val memesDir = File(storageDir, "memes")

    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        if (!dataFile.exists()) {
            dataFile.createNewFile()
            dataFile.writeText("[]")
        } else if (dataFile.readText().isBlank()) {
            dataFile.writeText("[]")
        }
        if (!memesDir.exists()) {
            memesDir.mkdirs()
        }
    }

    fun saveMemes(memes: List<Meme>) {
        val memes = memes.map { it.toSerializable() }
        val jsonString = json.encodeToString(memes)
        dataFile.writeText(jsonString)
    }

    fun loadMemes(): List<Meme> {
        val jsonString = dataFile.readText()
        val serializableMemes = json.decodeFromString<List<SerializableMeme>>(jsonString)
        return serializableMemes.map { it.toNormalize() }
    }

    fun addMeme(meme: Meme): List<Meme> {
        val memes = loadMemes().toMutableList()
        if (memes.none { it.id == meme.id }) {
            memes.add(meme)
        }
        saveMemes(memes)
        return memes
    }

    fun removeMeme(id: String): List<Meme> {
        val memes = loadMemes().filter { it.id != id }
        saveMemes(memes)
        return memes
    }

    fun updateMeme(id: String, updatedMeme: Meme): List<Meme> {
        val memes = loadMemes().map {
            if (it.id == id) updatedMeme else it
        }
        saveMemes(memes)
        return memes
    }

    fun checkDataConsistency() {
        val memes = loadMemes()
        val registeredFiles = memes.map { it.file.absolutePath }.toSet()

        val allFiles = memesDir.listFiles()?.filter { it.isFile }?.map { it.absolutePath }?.toSet() ?: emptySet()

        for (file in memesDir.listFiles()?.filter { it.isFile } ?: emptyList()) {
            if (file.absolutePath !in registeredFiles) {
                file.delete()
            }
        }

        val validMemes = mutableListOf<Meme>()

        for (meme in memes) {
            if (meme.file.absolutePath in allFiles) {
                validMemes.add(meme)
            }
        }

        if (validMemes.size != memes.size) {
            saveMemes(validMemes)
        }
    }

    @Serializable
    private data class SerializableMeme(
        val id: String,
        val filePath: String,
        val name: String,
        val collection: String = "default",
        val createdAt: String,
        val updatedAt: String
    )

    private fun Meme.toSerializable(): SerializableMeme {
        return SerializableMeme(
            id = this.id,
            filePath = this.file.absolutePath,
            name = this.name,
            collection = this.collection,
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString()
        )
    }

    private fun SerializableMeme.toNormalize(): Meme {
        return Meme(
            id = this.id,
            file = File(this.filePath),
            name = this.name,
            collection = this.collection,
            createdAt = LocalDateTime.parse(this.createdAt),
            updatedAt = LocalDateTime.parse(this.updatedAt)
        )
    }
}