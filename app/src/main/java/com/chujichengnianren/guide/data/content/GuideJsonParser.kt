package com.chujichengnianren.guide.data.content

import com.chujichengnianren.guide.data.model.GuideContent
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuideJsonParser @Inject constructor() {
    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
        explicitNulls = true
    }

    fun parse(rawJson: String): Result<GuideContent> = runCatching {
        json.decodeFromString<GuideContent>(rawJson)
    }
}

