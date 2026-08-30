package com.chujichengnianren.guide.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDestinationTest {
    @Test
    fun bottomNavigation_hasExactlyFourUniqueDestinations() {
        val destinations = TopLevelDestination.entries

        assertEquals(4, destinations.size)
        assertEquals(4, destinations.map { it.route }.distinct().size)
    }

    @Test
    fun routes_areCentralizedAndStable() {
        assertEquals(
            listOf("home", "search", "favorites", "profile"),
            TopLevelDestination.entries.map { it.route },
        )
        assertTrue(AppRoutes.category("housing").startsWith("category/"))
        assertTrue(AppRoutes.guide("renting").startsWith("guide/"))
    }
}
