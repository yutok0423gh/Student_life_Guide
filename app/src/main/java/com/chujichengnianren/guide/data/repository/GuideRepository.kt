package com.chujichengnianren.guide.data.repository

import com.chujichengnianren.guide.data.local.dao.ContentDao
import com.chujichengnianren.guide.data.local.entity.GuideEntity
import com.chujichengnianren.guide.data.local.toModel
import com.chujichengnianren.guide.data.model.GuideContent
import com.chujichengnianren.guide.data.model.Region
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class GuideListItem(
    val id: String,
    val title: String,
    val summary: String,
    val categoryId: String,
    val region: Region,
    val verifiedAt: String,
    val version: Int,
)

interface GuideRepository {
    fun observeAll(): Flow<List<GuideListItem>>

    fun observeByCategory(categoryId: String): Flow<List<GuideListItem>>

    suspend fun getGuide(guideId: String): GuideContent?
}

@Singleton
class LocalGuideRepository @Inject constructor(
    private val contentDao: ContentDao,
) : GuideRepository {
    override fun observeAll(): Flow<List<GuideListItem>> =
        contentDao.observeGuides().map { guides -> guides.map(GuideEntity::toListItem) }

    override fun observeByCategory(categoryId: String): Flow<List<GuideListItem>> =
        contentDao.observeGuidesByCategory(categoryId).map { guides -> guides.map(GuideEntity::toListItem) }

    override suspend fun getGuide(guideId: String): GuideContent? =
        contentDao.getGuide(guideId)?.toModel()
}

private fun GuideEntity.toListItem() = GuideListItem(
    id = id,
    title = title,
    summary = summary,
    categoryId = categoryId,
    region = Region(regionLevel, regionProvince, regionCity),
    verifiedAt = verifiedAt,
    version = contentVersion,
)

