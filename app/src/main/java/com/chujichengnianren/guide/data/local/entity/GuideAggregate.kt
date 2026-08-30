package com.chujichengnianren.guide.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation

data class GuideAggregate(
    @Embedded val guide: GuideEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "guide_id",
    )
    val steps: List<GuideStepEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "guide_id",
    )
    val checklist: List<ChecklistDefinitionEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "guide_id",
    )
    val sources: List<OfficialSourceEntity>,
)

data class GuideWithFavorite(
    @Embedded val guide: GuideEntity,
    @ColumnInfo(name = "favorited_at") val favoritedAt: Long,
)

data class GuideWithHistory(
    @Embedded val guide: GuideEntity,
    @ColumnInfo(name = "last_opened_at") val lastOpenedAt: Long,
)
