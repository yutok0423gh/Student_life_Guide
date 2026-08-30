package com.chujichengnianren.guide.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "guides",
    indices = [Index("category_id"), Index("region_level")],
)
data class GuideEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "scenario_ids") val scenarioIds: List<String>,
    val keywords: List<String>,
    val aliases: List<String>,
    @ColumnInfo(name = "region_level") val regionLevel: String,
    @ColumnInfo(name = "region_province") val regionProvince: String?,
    @ColumnInfo(name = "region_city") val regionCity: String?,
    @ColumnInfo(name = "quick_answer") val quickAnswer: String,
    @ColumnInfo(name = "verified_at") val verifiedAt: String,
    @ColumnInfo(name = "content_version") val contentVersion: Int,
)

@Entity(
    tableName = "guide_steps",
    primaryKeys = ["guide_id", "id"],
    foreignKeys = [
        ForeignKey(
            entity = GuideEntity::class,
            parentColumns = ["id"],
            childColumns = ["guide_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("guide_id")],
)
data class GuideStepEntity(
    @ColumnInfo(name = "guide_id") val guideId: String,
    val id: String,
    @ColumnInfo(name = "step_order") val order: Int,
    val title: String,
    val description: String,
)

@Entity(
    tableName = "checklist_definitions",
    primaryKeys = ["guide_id", "id"],
    foreignKeys = [
        ForeignKey(
            entity = GuideEntity::class,
            parentColumns = ["id"],
            childColumns = ["guide_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("guide_id")],
)
data class ChecklistDefinitionEntity(
    @ColumnInfo(name = "guide_id") val guideId: String,
    val id: String,
    val text: String,
    val required: Boolean,
)

@Entity(
    tableName = "official_sources",
    primaryKeys = ["guide_id", "id"],
    foreignKeys = [
        ForeignKey(
            entity = GuideEntity::class,
            parentColumns = ["id"],
            childColumns = ["guide_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("guide_id")],
)
data class OfficialSourceEntity(
    @ColumnInfo(name = "guide_id") val guideId: String,
    val id: String,
    @ColumnInfo(name = "source_name") val sourceName: String,
    @ColumnInfo(name = "source_title") val sourceTitle: String,
    @ColumnInfo(name = "source_url") val sourceUrl: String,
    @ColumnInfo(name = "source_level") val sourceLevel: String,
    @ColumnInfo(name = "published_at") val publishedAt: String?,
    @ColumnInfo(name = "verified_at") val verifiedAt: String,
)

