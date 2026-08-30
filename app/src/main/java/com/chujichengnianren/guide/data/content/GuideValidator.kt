package com.chujichengnianren.guide.data.content

import com.chujichengnianren.guide.data.model.ContentValues
import com.chujichengnianren.guide.data.model.GuideContent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

data class ValidationIssue(
    val code: String,
    val path: String,
    val message: String,
)

data class GuideValidationResult(
    val issues: List<ValidationIssue>,
) {
    val isValid: Boolean = issues.isEmpty()
}

@Singleton
class GuideValidator @Inject constructor() {
    fun validate(guide: GuideContent): GuideValidationResult {
        val issues = buildList {
            if (guide.id.isBlank()) {
                add(issue("EMPTY_GUIDE_ID", "id", "Guide id must not be blank."))
            }
            if (guide.title.isBlank()) {
                add(issue("EMPTY_TITLE", "title", "Guide title must not be blank."))
            }
            if (guide.categoryId !in ContentValues.categoryIds) {
                add(issue("INVALID_CATEGORY", "categoryId", "Unknown category id: ${guide.categoryId}"))
            }
            validateRegion(guide, this)
            validateUniqueIds(guide.steps.map { it.id }, "steps", "DUPLICATE_STEP_ID", this)
            validateUniqueIds(guide.checklist.map { it.id }, "checklist", "DUPLICATE_CHECKLIST_ID", this)
            validateUniqueIds(guide.sources.map { it.id }, "sources", "DUPLICATE_SOURCE_ID", this)

            if (!isIsoDate(guide.verifiedAt)) {
                add(issue("INVALID_VERIFIED_AT", "verifiedAt", "verifiedAt must use YYYY-MM-DD."))
            }
            if (guide.version <= 0) {
                add(issue("INVALID_VERSION", "version", "Guide version must be greater than zero."))
            }
            if (guide.warnings.isNotEmpty()) {
                add(issue("UNSUPPORTED_WARNINGS", "warnings", "Warning object schema is not defined yet."))
            }
            if (guide.sections.isNotEmpty()) {
                add(issue("UNSUPPORTED_SECTIONS", "sections", "Section object schema is not defined yet."))
            }

            guide.sources.forEachIndexed { index, source ->
                if (source.sourceLevel !in ContentValues.sourceLevels) {
                    add(
                        issue(
                            "INVALID_SOURCE_LEVEL",
                            "sources[$index].sourceLevel",
                            "Unknown source level: ${source.sourceLevel}",
                        ),
                    )
                }
                if (!isIsoDate(source.verifiedAt)) {
                    add(
                        issue(
                            "INVALID_SOURCE_VERIFIED_AT",
                            "sources[$index].verifiedAt",
                            "Source verifiedAt must use YYYY-MM-DD.",
                        ),
                    )
                }
                if (source.publishedAt != null && !isIsoDate(source.publishedAt)) {
                    add(
                        issue(
                            "INVALID_SOURCE_PUBLISHED_AT",
                            "sources[$index].publishedAt",
                            "Source publishedAt must use YYYY-MM-DD or null.",
                        ),
                    )
                }
            }
        }

        return GuideValidationResult(issues)
    }

    private fun validateRegion(
        guide: GuideContent,
        issues: MutableList<ValidationIssue>,
    ) {
        val region = guide.region
        if (region.level !in ContentValues.regionLevels) {
            issues += issue("INVALID_REGION_LEVEL", "region.level", "Unknown region level: ${region.level}")
            return
        }

        when (region.level) {
            "COUNTRY" -> if (region.province != null || region.city != null) {
                issues += issue(
                    "INVALID_COUNTRY_REGION",
                    "region",
                    "COUNTRY requires province and city to be null.",
                )
            }

            "PROVINCE" -> if (region.province.isNullOrBlank() || region.city != null) {
                issues += issue(
                    "INVALID_PROVINCE_REGION",
                    "region",
                    "PROVINCE requires a province and a null city.",
                )
            }

            "CITY" -> if (region.province.isNullOrBlank() || region.city.isNullOrBlank()) {
                issues += issue(
                    "INVALID_CITY_REGION",
                    "region",
                    "CITY requires both province and city.",
                )
            }
        }
    }

    private fun validateUniqueIds(
        ids: List<String>,
        path: String,
        code: String,
        issues: MutableList<ValidationIssue>,
    ) {
        ids.filter { it.isBlank() }.forEach {
            issues += issue("EMPTY_CHILD_ID", path, "IDs in $path must not be blank.")
        }
        ids.groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys
            .forEach { duplicate ->
                issues += issue(code, path, "Duplicate id in $path: $duplicate")
            }
    }

    private fun isIsoDate(value: String): Boolean {
        if (!ISO_DATE_PATTERN.matches(value)) return false
        return try {
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }

    private fun issue(code: String, path: String, message: String) =
        ValidationIssue(code = code, path = path, message = message)

    private companion object {
        val ISO_DATE_PATTERN = Regex("\\d{4}-\\d{2}-\\d{2}")
    }
}
