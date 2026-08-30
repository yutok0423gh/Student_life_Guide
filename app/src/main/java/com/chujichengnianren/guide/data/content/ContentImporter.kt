package com.chujichengnianren.guide.data.content

import android.util.Log
import com.chujichengnianren.guide.data.local.dao.ContentDao
import com.chujichengnianren.guide.data.local.toPersistence
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

data class ImportIssue(
    val fileName: String,
    val guideId: String?,
    val reasons: List<String>,
)

data class ImportReport(
    val discoveredCount: Int,
    val importedCount: Int,
    val unchangedCount: Int,
    val invalidCount: Int,
    val issues: List<ImportIssue>,
)

@Singleton
class ContentImporter @Inject constructor(
    private val contentDao: ContentDao,
    private val parser: GuideJsonParser,
    private val validator: GuideValidator,
) {
    private val importMutex = Mutex()

    suspend fun import(assets: List<RawGuideAsset>): ImportReport = importMutex.withLock {
        val issues = mutableListOf<ImportIssue>()
        val parsed = assets.mapNotNull { asset ->
            parser.parse(asset.json).fold(
                onSuccess = { asset to it },
                onFailure = { error ->
                    issues += ImportIssue(
                        fileName = asset.fileName,
                        guideId = null,
                        reasons = listOf("JSON_PARSE_ERROR: ${error.message.orEmpty()}"),
                    )
                    null
                },
            )
        }

        val duplicateGuideIds = parsed
            .groupingBy { (_, guide) -> guide.id }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        var importedCount = 0
        var unchangedCount = 0

        parsed.forEach { (asset, guide) ->
            if (guide.id in duplicateGuideIds) {
                issues += ImportIssue(
                    fileName = asset.fileName,
                    guideId = guide.id,
                    reasons = listOf("DUPLICATE_GUIDE_ID: ${guide.id}"),
                )
                return@forEach
            }

            val validation = validator.validate(guide)
            if (!validation.isValid) {
                issues += ImportIssue(
                    fileName = asset.fileName,
                    guideId = guide.id,
                    reasons = validation.issues.map { "${it.code} (${it.path}): ${it.message}" },
                )
                return@forEach
            }

            val currentVersion = contentDao.getContentVersion(guide.id)
            if (currentVersion != null && currentVersion >= guide.version) {
                unchangedCount += 1
                return@forEach
            }

            val persisted = guide.toPersistence()
            contentDao.replaceGuide(
                guide = persisted.guide,
                steps = persisted.steps,
                checklist = persisted.checklist,
                sources = persisted.sources,
            )
            importedCount += 1
        }

        issues.forEach { issue ->
            Log.w(TAG, "Skipped ${issue.fileName}: ${issue.reasons.joinToString()}")
        }

        ImportReport(
            discoveredCount = assets.size,
            importedCount = importedCount,
            unchangedCount = unchangedCount,
            invalidCount = issues.size,
            issues = issues,
        )
    }

    private companion object {
        const val TAG = "ContentImporter"
    }
}

