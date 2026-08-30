package com.chujichengnianren.guide.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favorites",
    primaryKeys = ["guide_id"],
    foreignKeys = [
        ForeignKey(
            entity = GuideEntity::class,
            parentColumns = ["id"],
            childColumns = ["guide_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class FavoriteEntity(
    @ColumnInfo(name = "guide_id") val guideId: String,
    @ColumnInfo(name = "favorited_at") val favoritedAt: Long,
)

@Entity(
    tableName = "checklist_states",
    primaryKeys = ["guide_id", "checklist_item_id"],
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
data class ChecklistStateEntity(
    @ColumnInfo(name = "guide_id") val guideId: String,
    @ColumnInfo(name = "checklist_item_id") val checklistItemId: String,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean,
)

@Entity(
    tableName = "reading_history",
    primaryKeys = ["guide_id"],
    foreignKeys = [
        ForeignKey(
            entity = GuideEntity::class,
            parentColumns = ["id"],
            childColumns = ["guide_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ReadingHistoryEntity(
    @ColumnInfo(name = "guide_id") val guideId: String,
    @ColumnInfo(name = "last_opened_at") val lastOpenedAt: Long,
)

