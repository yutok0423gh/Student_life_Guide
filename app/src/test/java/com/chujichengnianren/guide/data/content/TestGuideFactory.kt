package com.chujichengnianren.guide.data.content

import com.chujichengnianren.guide.data.model.ChecklistItem
import com.chujichengnianren.guide.data.model.GuideContent
import com.chujichengnianren.guide.data.model.GuideStep
import com.chujichengnianren.guide.data.model.OfficialSource
import com.chujichengnianren.guide.data.model.Region
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal fun testGuide(
    id: String = "guide_test",
    title: String = "测试手册",
    categoryId: String = "life",
    region: Region = Region("COUNTRY", null, null),
    verifiedAt: String = "2026-08-29",
    version: Int = 1,
) = GuideContent(
    id = id,
    title = title,
    summary = "用于数据层测试。",
    categoryId = categoryId,
    scenarioIds = listOf("test-scenario"),
    keywords = listOf("测试"),
    aliases = listOf("测试别名"),
    region = region,
    quickAnswer = "测试快速答案。",
    steps = listOf(
        GuideStep(
            id = "step_01",
            order = 1,
            title = "第一步",
            description = "完成测试。",
        ),
    ),
    checklist = listOf(
        ChecklistItem(
            id = "check_01",
            text = "测试材料",
            required = false,
        ),
    ),
    warnings = emptyList(),
    sections = emptyList(),
    sources = listOf(
        OfficialSource(
            id = "source_01",
            sourceName = "测试官方机构",
            sourceTitle = "测试来源",
            sourceUrl = "https://example.gov.cn/test",
            sourceLevel = "NATIONAL",
            publishedAt = null,
            verifiedAt = "2026-08-29",
        ),
    ),
    verifiedAt = verifiedAt,
    version = version,
)

internal fun GuideContent.toTestJson(): String = Json.encodeToString(this)

