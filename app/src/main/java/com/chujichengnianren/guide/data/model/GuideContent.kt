package com.chujichengnianren.guide.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GuideContent(
    val id: String,
    val title: String,
    val summary: String,
    val categoryId: String,
    val scenarioIds: List<String>,
    val keywords: List<String>,
    val aliases: List<String>,
    val region: Region,
    val quickAnswer: String,
    val steps: List<GuideStep>,
    val checklist: List<ChecklistItem>,
    val warnings: List<JsonElement>,
    val sections: List<JsonElement>,
    val sources: List<OfficialSource>,
    val verifiedAt: String,
    val version: Int,
)

@Serializable
data class Region(
    val level: String,
    val province: String?,
    val city: String?,
)

@Serializable
data class GuideStep(
    val id: String,
    val order: Int,
    val title: String,
    val description: String,
)

@Serializable
data class ChecklistItem(
    val id: String,
    val text: String,
    val required: Boolean,
)

@Serializable
data class OfficialSource(
    val id: String,
    val sourceName: String,
    val sourceTitle: String,
    val sourceUrl: String,
    val sourceLevel: String,
    val publishedAt: String?,
    val verifiedAt: String,
)

object ContentValues {
    val categoryIds = setOf(
        "education",
        "housing",
        "health",
        "finance",
        "career",
        "documents",
        "safety",
        "life",
    )

    val regionLevels = setOf("COUNTRY", "PROVINCE", "CITY")

    val sourceLevels = setOf("NATIONAL", "PROVINCIAL", "CITY", "OTHER_OFFICIAL")
}

