package com.chujichengnianren.guide.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.chujichengnianren.guide.data.local.entity.ChecklistStateEntity
import com.chujichengnianren.guide.data.local.entity.FavoriteEntity
import com.chujichengnianren.guide.data.local.entity.GuideWithFavorite
import com.chujichengnianren.guide.data.local.entity.GuideWithHistory
import com.chujichengnianren.guide.data.local.entity.ReadingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStateDao {
    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE guide_id = :guideId")
    suspend fun deleteFavorite(guideId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE guide_id = :guideId)")
    fun observeIsFavorite(guideId: String): Flow<Boolean>

    @Query(
        """
        SELECT guides.*, favorites.favorited_at
        FROM favorites
        INNER JOIN guides ON guides.id = favorites.guide_id
        ORDER BY favorites.favorited_at DESC
        """,
    )
    fun observeFavorites(): Flow<List<GuideWithFavorite>>

    @Upsert
    suspend fun upsertChecklistState(state: ChecklistStateEntity)

    @Query("SELECT * FROM checklist_states WHERE guide_id = :guideId")
    fun observeChecklistStates(guideId: String): Flow<List<ChecklistStateEntity>>

    @Upsert
    suspend fun upsertReadingHistory(history: ReadingHistoryEntity)

    @Query(
        """
        SELECT guides.*, reading_history.last_opened_at
        FROM reading_history
        INNER JOIN guides ON guides.id = reading_history.guide_id
        ORDER BY reading_history.last_opened_at DESC
        LIMIT :limit
        """,
    )
    fun observeRecentGuides(limit: Int): Flow<List<GuideWithHistory>>

    @Query("SELECT * FROM reading_history WHERE guide_id = :guideId LIMIT 1")
    suspend fun getReadingHistory(guideId: String): ReadingHistoryEntity?
}

