package com.chujichengnianren.guide.data.content

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chujichengnianren.guide.data.local.GuideDatabase
import com.chujichengnianren.guide.data.repository.EpochMillisProvider
import com.chujichengnianren.guide.data.repository.LocalUserStateRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = Application::class)
class ContentImporterRoomTest {
    private lateinit var database: GuideDatabase
    private lateinit var importer: ContentImporter
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, GuideDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        importer = ContentImporter(
            contentDao = database.contentDao(),
            parser = GuideJsonParser(),
            validator = GuideValidator(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun bundledAsset_importsIntoRoomWithChildren() = runBlocking {
        val assets = BundledGuideAssetLoader(context).load()
        val expectedGuides = assets.map { asset ->
            GuideJsonParser().parse(asset.json).getOrThrow()
        }

        val report = importer.import(assets)

        assertTrue(assets.isNotEmpty())
        assertEquals(assets.size, report.importedCount)
        assertEquals(0, report.invalidCount)
        assertEquals(assets.size, database.contentDao().countGuides())

        expectedGuides.forEach { expected ->
            val stored = database.contentDao().getGuide(expected.id)

            assertNotNull("Missing bundled guide ${expected.id}", stored)
            assertEquals(expected.steps.size, stored?.steps?.size)
            assertEquals(expected.checklist.size, stored?.checklist?.size)
            assertEquals(expected.sources.size, stored?.sources?.size)
        }
    }

    @Test
    fun invalidGuide_isSkippedWhileValidGuideStillImports() = runBlocking {
        val assets = listOf(
            RawGuideAsset("valid.json", testGuide(id = "guide_valid").toTestJson()),
            RawGuideAsset(
                "invalid.json",
                testGuide(id = "guide_invalid", categoryId = "not-a-category").toTestJson(),
            ),
        )

        val report = importer.import(assets)

        assertEquals(1, report.importedCount)
        assertEquals(1, report.invalidCount)
        assertEquals(1, database.contentDao().countGuides())
        assertNotNull(database.contentDao().getGuide("guide_valid"))
    }

    @Test
    fun duplicateGuideIds_areDetectedAndNeitherCopyImports() = runBlocking {
        val raw = testGuide(id = "guide_duplicate").toTestJson()

        val report = importer.import(
            listOf(
                RawGuideAsset("first.json", raw),
                RawGuideAsset("second.json", raw),
            ),
        )

        assertEquals(0, report.importedCount)
        assertEquals(2, report.invalidCount)
        assertTrue(report.issues.all { issue -> issue.reasons.any { "DUPLICATE_GUIDE_ID" in it } })
        assertEquals(0, database.contentDao().countGuides())
    }

    @Test
    fun contentVersion_onlyAllowsNewerBundledGuideToReplaceStoredGuide() = runBlocking {
        importer.import(
            listOf(RawGuideAsset("guide.json", testGuide(title = "版本一").toTestJson())),
        )

        val sameVersion = importer.import(
            listOf(RawGuideAsset("guide.json", testGuide(title = "不应覆盖").toTestJson())),
        )
        val afterSameVersion = database.contentDao().getGuide("guide_test")

        val newerVersion = importer.import(
            listOf(
                RawGuideAsset(
                    "guide.json",
                    testGuide(title = "版本二", version = 2).toTestJson(),
                ),
            ),
        )
        val afterNewerVersion = database.contentDao().getGuide("guide_test")

        assertEquals(1, sameVersion.unchangedCount)
        assertEquals("版本一", afterSameVersion?.guide?.title)
        assertEquals(1, newerVersion.importedCount)
        assertEquals("版本二", afterNewerVersion?.guide?.title)
        assertEquals(2, afterNewerVersion?.guide?.contentVersion)
    }

    @Test
    fun favoriteChecklistAndHistoryState_arePersistedInRoom() = runBlocking {
        importer.import(
            listOf(RawGuideAsset("guide.json", testGuide().toTestJson())),
        )
        val clock = FakeClock(1_000L)
        val repository = LocalUserStateRepository(database.userStateDao(), clock)

        repository.setFavorite("guide_test", true)
        repository.setChecklistItemChecked("guide_test", "check_01", true)
        repository.recordGuideOpened("guide_test")

        assertTrue(repository.observeIsFavorite("guide_test").first())
        assertEquals(listOf("guide_test"), repository.observeFavorites().first().map { it.id })
        assertEquals(true, repository.observeChecklistStates("guide_test").first()["check_01"])
        assertEquals(1_000L, database.userStateDao().getReadingHistory("guide_test")?.lastOpenedAt)

        clock.value = 2_000L
        repository.recordGuideOpened("guide_test")
        repository.setFavorite("guide_test", false)

        assertFalse(repository.observeIsFavorite("guide_test").first())
        assertEquals(2_000L, database.userStateDao().getReadingHistory("guide_test")?.lastOpenedAt)
        assertEquals(listOf("guide_test"), repository.observeRecentGuides().first().map { it.id })
    }
}

private class FakeClock(var value: Long) : EpochMillisProvider {
    override fun now(): Long = value
}
