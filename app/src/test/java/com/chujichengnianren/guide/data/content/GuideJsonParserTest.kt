package com.chujichengnianren.guide.data.content

import com.chujichengnianren.guide.data.model.ChecklistItem
import com.chujichengnianren.guide.data.model.GuideStep
import com.chujichengnianren.guide.data.model.Region
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuideJsonParserTest {
    private val parser = GuideJsonParser()
    private val validator = GuideValidator()

    @Test
    fun validGuide_parsesAndValidates() {
        val parsed = parser.parse(testGuide().toTestJson()).getOrThrow()

        assertEquals("guide_test", parsed.id)
        assertTrue(validator.validate(parsed).isValid)
    }

    @Test
    fun invalidJson_returnsFailureWithoutThrowing() {
        val result = parser.parse("{not valid json")

        assertTrue(result.isFailure)
    }

    @Test
    fun unknownJsonKey_isRejected() {
        val raw = testGuide().toTestJson().dropLast(1) + ",\"guessedKey\":true}"

        assertTrue(parser.parse(raw).isFailure)
    }

    @Test
    fun invalidCategory_isRejected() {
        val result = validator.validate(testGuide(categoryId = "unknown"))

        assertFalse(result.isValid)
        assertTrue(result.issues.any { it.code == "INVALID_CATEGORY" })
    }

    @Test
    fun invalidRegionLevel_isRejected() {
        val result = validator.validate(testGuide(region = Region("DISTRICT", null, null)))

        assertFalse(result.isValid)
        assertTrue(result.issues.any { it.code == "INVALID_REGION_LEVEL" })
    }

    @Test
    fun regionShape_mustMatchItsLevel() {
        val result = validator.validate(testGuide(region = Region("CITY", "广东省", null)))

        assertFalse(result.isValid)
        assertTrue(result.issues.any { it.code == "INVALID_CITY_REGION" })
    }

    @Test
    fun duplicateStepId_isRejected() {
        val guide = testGuide().let { original ->
            original.copy(steps = original.steps + original.steps.first().copy(order = 2))
        }

        assertTrue(validator.validate(guide).issues.any { it.code == "DUPLICATE_STEP_ID" })
    }

    @Test
    fun duplicateChecklistId_isRejected() {
        val guide = testGuide().let { original ->
            original.copy(
                checklist = original.checklist + ChecklistItem("check_01", "重复", true),
            )
        }

        assertTrue(validator.validate(guide).issues.any { it.code == "DUPLICATE_CHECKLIST_ID" })
    }

    @Test
    fun duplicateSourceId_isRejected() {
        val guide = testGuide().let { original ->
            original.copy(sources = original.sources + original.sources.first())
        }

        assertTrue(validator.validate(guide).issues.any { it.code == "DUPLICATE_SOURCE_ID" })
    }

    @Test
    fun verifiedAt_requiresRealIsoDate() {
        val wrongFormat = validator.validate(testGuide(verifiedAt = "29/08/2026"))
        val impossibleDate = validator.validate(testGuide(verifiedAt = "2026-02-30"))

        assertTrue(wrongFormat.issues.any { it.code == "INVALID_VERIFIED_AT" })
        assertTrue(impossibleDate.issues.any { it.code == "INVALID_VERIFIED_AT" })
    }

    @Test
    fun unresolvedWarningAndSectionSchemas_onlyAllowEmptyArrays() {
        val withWarning = testGuide().copy(warnings = listOf(JsonPrimitive("not-defined")))
        val withSection = testGuide().copy(sections = listOf(JsonPrimitive("not-defined")))

        assertTrue(validator.validate(withWarning).issues.any { it.code == "UNSUPPORTED_WARNINGS" })
        assertTrue(validator.validate(withSection).issues.any { it.code == "UNSUPPORTED_SECTIONS" })
    }

    @Test
    fun stepOrder_isPreservedAsExplicitData() {
        val guide = testGuide().copy(
            steps = listOf(
                GuideStep("step_later", 20, "后一步", "后一步说明"),
                GuideStep("step_first", 10, "先一步", "先一步说明"),
            ),
        )

        assertTrue(validator.validate(guide).isValid)
        assertEquals(listOf(20, 10), guide.steps.map { it.order })
    }
}
