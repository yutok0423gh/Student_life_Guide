package com.chujichengnianren.guide.data.local

import com.chujichengnianren.guide.data.local.entity.ChecklistDefinitionEntity
import com.chujichengnianren.guide.data.local.entity.GuideAggregate
import com.chujichengnianren.guide.data.local.entity.GuideEntity
import com.chujichengnianren.guide.data.local.entity.GuideStepEntity
import com.chujichengnianren.guide.data.local.entity.OfficialSourceEntity
import com.chujichengnianren.guide.data.model.ChecklistItem
import com.chujichengnianren.guide.data.model.GuideContent
import com.chujichengnianren.guide.data.model.GuideStep
import com.chujichengnianren.guide.data.model.OfficialSource
import com.chujichengnianren.guide.data.model.Region

data class PersistedGuide(
    val guide: GuideEntity,
    val steps: List<GuideStepEntity>,
    val checklist: List<ChecklistDefinitionEntity>,
    val sources: List<OfficialSourceEntity>,
)

fun GuideContent.toPersistence(): PersistedGuide = PersistedGuide(
    guide = GuideEntity(
        id = id,
        title = title,
        summary = summary,
        categoryId = categoryId,
        scenarioIds = scenarioIds,
        keywords = keywords,
        aliases = aliases,
        regionLevel = region.level,
        regionProvince = region.province,
        regionCity = region.city,
        quickAnswer = quickAnswer,
        verifiedAt = verifiedAt,
        contentVersion = version,
    ),
    steps = steps.map { step ->
        GuideStepEntity(
            guideId = id,
            id = step.id,
            order = step.order,
            title = step.title,
            description = step.description,
        )
    },
    checklist = checklist.map { item ->
        ChecklistDefinitionEntity(
            guideId = id,
            id = item.id,
            text = item.text,
            required = item.required,
        )
    },
    sources = sources.map { source ->
        OfficialSourceEntity(
            guideId = id,
            id = source.id,
            sourceName = source.sourceName,
            sourceTitle = source.sourceTitle,
            sourceUrl = source.sourceUrl,
            sourceLevel = source.sourceLevel,
            publishedAt = source.publishedAt,
            verifiedAt = source.verifiedAt,
        )
    },
)

fun GuideAggregate.toModel(): GuideContent = GuideContent(
    id = guide.id,
    title = guide.title,
    summary = guide.summary,
    categoryId = guide.categoryId,
    scenarioIds = guide.scenarioIds,
    keywords = guide.keywords,
    aliases = guide.aliases,
    region = Region(
        level = guide.regionLevel,
        province = guide.regionProvince,
        city = guide.regionCity,
    ),
    quickAnswer = guide.quickAnswer,
    steps = steps
        .sortedWith(compareBy(GuideStepEntity::order, GuideStepEntity::id))
        .map { GuideStep(it.id, it.order, it.title, it.description) },
    checklist = checklist
        .sortedBy(ChecklistDefinitionEntity::id)
        .map { ChecklistItem(it.id, it.text, it.required) },
    warnings = emptyList(),
    sections = emptyList(),
    sources = sources
        .sortedBy(OfficialSourceEntity::id)
        .map {
            OfficialSource(
                id = it.id,
                sourceName = it.sourceName,
                sourceTitle = it.sourceTitle,
                sourceUrl = it.sourceUrl,
                sourceLevel = it.sourceLevel,
                publishedAt = it.publishedAt,
                verifiedAt = it.verifiedAt,
            )
        },
    verifiedAt = guide.verifiedAt,
    version = guide.contentVersion,
)

