package com.chujichengnianren.guide.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME(AppRoutes.HOME, "首页", Icons.Filled.Home),
    SEARCH(AppRoutes.SEARCH, "搜索", Icons.Filled.Search),
    FAVORITES(AppRoutes.FAVORITES, "收藏", Icons.Filled.FavoriteBorder),
    PROFILE(AppRoutes.PROFILE, "我的", Icons.Filled.Person),
}

object AppRoutes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"

    const val CATEGORY_ID = "categoryId"
    const val CATEGORY_PATTERN = "category/{$CATEGORY_ID}"

    const val GUIDE_ID = "guideId"
    const val GUIDE_PATTERN = "guide/{$GUIDE_ID}"

    const val EMERGENCY = "emergency"
    const val REGION_PICKER = "region"
    const val STAGE_PICKER = "stage"

    fun category(categoryId: String): String = "category/$categoryId"

    fun guide(guideId: String): String = "guide/$guideId"
}

