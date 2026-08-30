package com.chujichengnianren.guide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chujichengnianren.guide.ui.category.CategoryScreen
import com.chujichengnianren.guide.ui.emergency.EmergencyScreen
import com.chujichengnianren.guide.ui.favorite.FavoritesScreen
import com.chujichengnianren.guide.ui.guide.GuideDetailScreen
import com.chujichengnianren.guide.ui.home.HomeScreen
import com.chujichengnianren.guide.ui.profile.ProfileScreen
import com.chujichengnianren.guide.ui.profile.RegionPickerScreen
import com.chujichengnianren.guide.ui.profile.StagePickerScreen
import com.chujichengnianren.guide.ui.search.SearchScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME,
        modifier = modifier,
    ) {
        composable(AppRoutes.HOME) {
            HomeScreen(
                onSearchClick = { navController.navigate(AppRoutes.SEARCH) },
                onEmergencyClick = { navController.navigate(AppRoutes.EMERGENCY) },
                onScenarioClick = { navController.navigate(AppRoutes.guide(it)) },
                onCategoryClick = { navController.navigate(AppRoutes.category(it)) },
                onRegionClick = { navController.navigate(AppRoutes.REGION_PICKER) },
                onStageClick = { navController.navigate(AppRoutes.STAGE_PICKER) },
            )
        }
        composable(AppRoutes.SEARCH) {
            SearchScreen(onGuideClick = { navController.navigate(AppRoutes.guide(it)) })
        }
        composable(AppRoutes.FAVORITES) {
            FavoritesScreen()
        }
        composable(AppRoutes.PROFILE) {
            ProfileScreen(
                onRegionClick = { navController.navigate(AppRoutes.REGION_PICKER) },
                onStageClick = { navController.navigate(AppRoutes.STAGE_PICKER) },
                onFavoritesClick = { navController.navigate(AppRoutes.FAVORITES) },
            )
        }
        composable(
            route = AppRoutes.CATEGORY_PATTERN,
            arguments = listOf(navArgument(AppRoutes.CATEGORY_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            CategoryScreen(
                categoryId = backStackEntry.arguments?.getString(AppRoutes.CATEGORY_ID).orEmpty(),
                onBackClick = navController::navigateUp,
                onGuideClick = { navController.navigate(AppRoutes.guide(it)) },
            )
        }
        composable(
            route = AppRoutes.GUIDE_PATTERN,
            arguments = listOf(navArgument(AppRoutes.GUIDE_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            GuideDetailScreen(
                guideId = backStackEntry.arguments?.getString(AppRoutes.GUIDE_ID).orEmpty(),
                onBackClick = navController::navigateUp,
            )
        }
        composable(AppRoutes.EMERGENCY) {
            EmergencyScreen(onBackClick = navController::navigateUp)
        }
        composable(AppRoutes.REGION_PICKER) {
            RegionPickerScreen(onBackClick = navController::navigateUp)
        }
        composable(AppRoutes.STAGE_PICKER) {
            StagePickerScreen(onBackClick = navController::navigateUp)
        }
    }
}
