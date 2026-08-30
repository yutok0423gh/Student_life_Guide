package com.chujichengnianren.guide.data.content

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class RawGuideAsset(
    val fileName: String,
    val json: String,
)

@Singleton
class BundledGuideAssetLoader @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    suspend fun load(): List<RawGuideAsset> = withContext(Dispatchers.IO) {
        val files = context.assets.list(GUIDES_PATH)
            .orEmpty()
            .filter { it.endsWith(".json", ignoreCase = true) }
            .sorted()

        files.map { fileName ->
            val path = "$GUIDES_PATH/$fileName"
            RawGuideAsset(
                fileName = fileName,
                json = context.assets.open(path).bufferedReader().use { it.readText() },
            )
        }
    }

    private companion object {
        const val GUIDES_PATH = "content/guides"
    }
}
