package com.chujichengnianren.guide.data.content

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BundledContentInitializer @Inject constructor(
    private val loader: BundledGuideAssetLoader,
    private val importer: ContentImporter,
) {
    @Volatile
    private var initialized = false

    suspend fun initialize(): ImportReport {
        if (initialized) return EMPTY_REPORT

        return runCatching {
            importer.import(loader.load())
        }.onSuccess { report ->
            initialized = true
            Log.i(
                TAG,
                "Bundled content: ${report.importedCount} imported, " +
                    "${report.unchangedCount} unchanged, ${report.invalidCount} invalid.",
            )
        }.getOrElse { error ->
            Log.e(TAG, "Bundled content initialization failed without crashing the app.", error)
            EMPTY_REPORT.copy(
                invalidCount = 1,
                issues = listOf(
                    ImportIssue(
                        fileName = "content/guides",
                        guideId = null,
                        reasons = listOf("ASSET_LOAD_ERROR: ${error.message.orEmpty()}"),
                    ),
                ),
            )
        }
    }

    private companion object {
        const val TAG = "ContentInitializer"
        val EMPTY_REPORT = ImportReport(
            discoveredCount = 0,
            importedCount = 0,
            unchangedCount = 0,
            invalidCount = 0,
            issues = emptyList(),
        )
    }
}
