package com.chujichengnianren.guide.data.repository

import com.chujichengnianren.guide.data.local.dao.UserStateDao
import com.chujichengnianren.guide.data.local.entity.ChecklistStateEntity
import com.chujichengnianren.guide.data.local.entity.FavoriteEntity
import com.chujichengnianren.guide.data.local.entity.GuideEntity
import com.chujichengnianren.guide.data.local.entity.ReadingHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface EpochMillisProvider {
    fun now(): Long
}

class SystemEpochMillisProvider @Inject constructor() : EpochMillisProvider {
    override fun now(): Long = System.currentTimeMillis()
}

interface UserStateRepository {
    fun observeIsFavorite(guideId: String): Flow<Boolean>

    fun observeFavorites(): Flow<List<GuideListItem>>

    suspend fun setFavorite(guideId: String, isFavorite: Boolean)

    fun observeChecklistStates(guideId: String): Flow<Map<String, Boolean>>

    suspend fun setChecklistItemChecked(guideId: String, checklistItemId: String, isChecked: Boolean)

    fun observeRecentGuides(limit: Int = 10): Flow<List<GuideListItem>>

    suspend fun recordGuideOpened(guideId: String)
}

@Singleton
class LocalUserStateRepository @Inject constructor(
    private val dao: UserStateDao,
    private val clock: EpochMillisProvider,
) : UserStateRepository {
    override fun observeIsFavorite(guideId: String): Flow<Boolean> = dao.observeIsFavorite(guideId)

    override fun observeFavorites(): Flow<List<GuideListItem>> =
        dao.observeFavorites().map { rows -> rows.map { it.guide.toListItem() } }

    override suspend fun setFavorite(guideId: String, isFavorite: Boolean) {
        if (isFavorite) {
            dao.upsertFavorite(FavoriteEntity(guideId = guideId, favoritedAt = clock.now()))
        } else {
            dao.deleteFavorite(guideId)
        }
    }

    override fun observeChecklistStates(guideId: String): Flow<Map<String, Boolean>> =
        dao.observeChecklistStates(guideId).map { states ->
            states.associate { it.checklistItemId to it.isChecked }
        }

    override suspend fun setChecklistItemChecked(
        guideId: String,
        checklistItemId: String,
        isChecked: Boolean,
    ) {
        dao.upsertChecklistState(
            ChecklistStateEntity(
                guideId = guideId,
                checklistItemId = checklistItemId,
                isChecked = isChecked,
            ),
        )
    }

    override fun observeRecentGuides(limit: Int): Flow<List<GuideListItem>> =
        dao.observeRecentGuides(limit).map { rows -> rows.map { it.guide.toListItem() } }

    override suspend fun recordGuideOpened(guideId: String) {
        dao.upsertReadingHistory(
            ReadingHistoryEntity(
                guideId = guideId,
                lastOpenedAt = clock.now(),
            ),
        )
    }
}

private fun GuideEntity.toListItem() = GuideListItem(
    id = id,
    title = title,
    summary = summary,
    categoryId = categoryId,
    region = com.chujichengnianren.guide.data.model.Region(
        level = regionLevel,
        province = regionProvince,
        city = regionCity,
    ),
    verifiedAt = verifiedAt,
    version = contentVersion,
)

