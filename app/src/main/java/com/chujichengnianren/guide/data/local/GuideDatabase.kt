package com.chujichengnianren.guide.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chujichengnianren.guide.data.local.dao.ContentDao
import com.chujichengnianren.guide.data.local.dao.UserStateDao
import com.chujichengnianren.guide.data.local.entity.ChecklistDefinitionEntity
import com.chujichengnianren.guide.data.local.entity.ChecklistStateEntity
import com.chujichengnianren.guide.data.local.entity.FavoriteEntity
import com.chujichengnianren.guide.data.local.entity.GuideEntity
import com.chujichengnianren.guide.data.local.entity.GuideStepEntity
import com.chujichengnianren.guide.data.local.entity.OfficialSourceEntity
import com.chujichengnianren.guide.data.local.entity.ReadingHistoryEntity

@Database(
    entities = [
        GuideEntity::class,
        GuideStepEntity::class,
        ChecklistDefinitionEntity::class,
        OfficialSourceEntity::class,
        FavoriteEntity::class,
        ChecklistStateEntity::class,
        ReadingHistoryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(ContentTypeConverters::class)
abstract class GuideDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    abstract fun userStateDao(): UserStateDao

    companion object {
        const val DATABASE_NAME = "adult_starter_guide.db"
    }
}
