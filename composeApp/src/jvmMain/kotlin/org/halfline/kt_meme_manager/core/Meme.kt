package org.halfline.kt_meme_manager.core

import java.io.File
import java.time.LocalDateTime

data class Meme(
    val id: String = java.util.UUID.randomUUID().toString(),
    val file: File,
    val name: String,
    val collection: String = "default",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)