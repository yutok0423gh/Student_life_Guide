package com.chujichengnianren.guide.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class ContentTypeConverters {
    private val json = Json

    @TypeConverter
    fun stringListToJson(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun jsonToStringList(value: String): List<String> = json.decodeFromString(value)
}

