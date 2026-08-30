package com.chujichengnianren.guide.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.chujichengnianren.guide.data.local.entity.ChecklistDefinitionEntity
import com.chujichengnianren.guide.data.local.entity.GuideAggregate
import com.chujichengnianren.guide.data.local.entity.GuideEntity
import com.chujichengnianren.guide.data.local.entity.GuideStepEntity
import com.chujichengnianren.guide.data.local.entity.OfficialSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Query("SELECT * FROM guides ORDER BY title COLLATE NOCASE")
    fun observeGuides(): Flow<List<GuideEntity>>

    @Query("SELECT * FROM guides WHERE category_id = :categoryId ORDER BY title COLLATE NOCASE")
    fun observeGuidesByCategory(categoryId: String): Flow<List<GuideEntity>>

    @Transaction
    @Query("SELECT * FROM guides WHERE id = :guideId LIMIT 1")
    suspend fun getGuide(guideId: String): GuideAggregate?

    @Query("SELECT content_version FROM guides WHERE id = :guideId LIMIT 1")
    suspend fun getContentVersion(guideId: String): Int?

    @Query("SELECT COUNT(*) FROM guides")
    suspend fun countGuides(): Int

    @Upsert
    suspend fun upsertGuide(guide: GuideEntity)

    @Upsert
    suspend fun upsertSteps(steps: List<GuideStepEntity>)

    @Upsert
    suspend fun upsertChecklist(items: List<ChecklistDefinitionEntity>)

    @Upsert
    suspend fun upsertSources(sources: List<OfficialSourceEntity>)

    @Query("DELETE FROM guide_steps WHERE guide_id = :guideId")
    suspend fun deleteSteps(guideId: String)

    @Query("DELETE FROM checklist_definitions WHERE guide_id = :guideId")
    suspend fun deleteChecklist(guideId: String)

    @Query("DELETE FROM official_sources WHERE guide_id = :guideId")
    suspend fun deleteSources(guideId: String)

    @Transaction
    suspend fun replaceGuide(
        guide: GuideEntity,
        steps: List<GuideStepEntity>,
        checklist: List<ChecklistDefinitionEntity>,
        sources: List<OfficialSourceEntity>,
    ) {
        upsertGuide(guide)
        deleteSteps(guide.id)
        deleteChecklist(guide.id)
        deleteSources(guide.id)
        if (steps.isNotEmpty()) upsertSteps(steps)
        if (checklist.isNotEmpty()) upsertChecklist(checklist)
        if (sources.isNotEmpty()) upsertSources(sources)
    }
}

